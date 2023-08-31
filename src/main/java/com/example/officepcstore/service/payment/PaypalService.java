package com.example.officepcstore.service.payment;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.repository.OrderProductRepository;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.repository.UserRepository;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.service.paymentconfig.PaypalForm;
import com.example.officepcstore.service.paymentconfig.PaypalMethod;
import com.example.officepcstore.utils.CheckTimePayment;
import com.example.officepcstore.utils.ExchangeMoneyUtils;
import com.example.officepcstore.utils.PayUtils;
import com.example.officepcstore.utils.StringUtils;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class PaypalService extends PaymentSteps {
    private APIContext apiContext;
    private PayUtils payUtils;
    private final OrderRepository orderRepository;
    public static final String URL_SUCCESS_PAYPAL = "/api/checkout/paypal/success";
    public static final String URL_CANCEL_PAYPAL = "/api/checkout/paypal/cancel";
    public static final String PATTERN = "&token=";
    private final TaskScheduler taskScheduler;
    private final CheckTimePayment checkTimePayment;
    @Override
    @Transactional
    public ResponseEntity<?> initializationPayment(HttpServletRequest request, Order order) {
        String cancelUrl = StringUtils.getBaseURL(request) + URL_CANCEL_PAYPAL;
        String successUrl = StringUtils.getBaseURL(request) + URL_SUCCESS_PAYPAL;
        try {
            Payment payment = createPayPalPaymentSandBox(
                    order,
                    "USD",
                    PaypalMethod.paypal,
                    PaypalForm.sale,
                    "Thanh toan don hang  "+ order.getId(),
                    cancelUrl,
                    successUrl);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order, true);
                    String checkUpdateSold =payUtils.updateSoldProduct(order,true);
                    if (checkUpdateQuantityProduct == null && checkUpdateSold == null) {
                        if (!payment.getTransactions().isEmpty())
                            order.getPaymentInformation().getPaymentInfo().put("amount", payment.getTransactions().get(0).getAmount());
                        order.getPaymentInformation().setPaymentId(payment.getId());
                        order.getPaymentInformation().setPaymentToken((links.getHref().split(PATTERN)[1]));
                        order.getPaymentInformation().getPaymentInfo().put("isPaid", false);
                        orderRepository.save(order);
                        checkTimePayment.setOrderId(order.getId());
                        checkTimePayment.setOrderRepository(orderRepository);
                        taskScheduler.schedule(checkTimePayment, new Date(System.currentTimeMillis() + Constant.PAYMENT_TIMEOUT)) ;
                        return ResponseEntity.status(HttpStatus.OK).body(
                                new ResponseObjectData(true, "Payment complete", links.getHref()));
                    }
                }
            }
        } catch (PayPalRESTException | IOException e) {
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
        }
        return null;
    }

    @Override
    @SneakyThrows
    public ResponseEntity<?> makePayment(String paymentId, String payerId, String responseCode, String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            Payment payment= execute(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                String paymentToken = "EC-" + payment.getCart();
                Optional<Order> order = orderRepository.findOrderByPaymentInformation_PaymentTokenAndState(paymentToken, Constant.ORDER_PROCESS);
                if (order.isPresent()) {
                    order.get().getPaymentInformation().getPaymentInfo().put("payer", payment.getPayer().getPayerInfo());
                    order.get().getPaymentInformation().getPaymentInfo().put("paymentMethod", payment.getPayer().getPaymentMethod());
                    order.get().getPaymentInformation().getPaymentInfo().put("isPaid", true);
                    order.get().setState(Constant.ORDER_PAY_ONLINE);
                    orderRepository.save(order.get());
                } else {
                    response.sendRedirect(SelectPaymentService.URL_PAYMENT + "false&cancel=false");
                    throw new NotFoundException("Can not found order with id: " + id);
                }
                response.sendRedirect(SelectPaymentService.URL_PAYMENT + "true&cancel=false");
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Payment with Paypal complete", "")
                );
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        response.sendRedirect(SelectPaymentService.URL_PAYMENT + "false&cancel=false");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                new ResponseObjectData(false, "Payment with Paypal failed", "")
        );
    }


    @Override
    @SneakyThrows
    public ResponseEntity<?> cancelPayment(String id, String responseCode, HttpServletResponse response) {
        Optional<Order> order = orderRepository.findOrderByPaymentInformation_PaymentTokenAndState(id, Constant.ORDER_PROCESS);
        if (order.isPresent()) {
            order.get().setState(Constant.ORDER_CANCEL);
            orderRepository.save(order.get());
            String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
            String checkUpdateSold =payUtils.updateSoldProduct(order.get(),false);
            if (checkUpdateQuantityProduct == null && checkUpdateSold ==null) {
                response.sendRedirect(SelectPaymentService.URL_PAYMENT + "true&cancel=true");
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Cancel payment with Paypal complete", "")
                );
            }
        }
        response.sendRedirect(SelectPaymentService.URL_PAYMENT   + "false&cancel=true");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                new ResponseObjectData(false, "Cancel payment with Paypal failed", "")
        );
    }

    public Payment createPayPalPaymentSandBox(Order order, String currency, PaypalMethod method,
                                              PaypalForm paypalForm, String description, String cancelUrl,
                                              String successUrl) throws PayPalRESTException, IOException {
        double TotalMoneyVN= ExchangeMoneyUtils.exchange(order.getTotalPrice().add(new BigDecimal(order.getShippingDetail().getShipInfo().get("fee").toString())));
        Amount amount = new Amount(currency, String.format("%.2f", TotalMoneyVN));
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        Payment payment = new Payment(paypalForm.toString(),payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        apiContext.setMaskRequestId(true);
        return payment.create(apiContext);
    }

    public Payment execute(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }
}
