package com.example.officepcstore.service.payment;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;

import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.service.MailService;
import com.example.officepcstore.service.OrderSendMail;
import com.example.officepcstore.service.paymentconfig.PaypalForm;
import com.example.officepcstore.service.paymentconfig.PaypalMethod;

import com.example.officepcstore.utils.ExchangeMoneyUtils;
import com.example.officepcstore.utils.PayUtils;
import com.example.officepcstore.utils.StringUtils;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AllArgsConstructor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
    private final OrderSendMail orderSendMail;
    private final MailService mailService;

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
                    String putQuantity = payUtils.checkStockAndQuantityToUpdateProduct(order, true);
                    String putSold =payUtils.putSold(order,true);
                    if (putQuantity == null && putSold == null) {
                        if (!payment.getTransactions().isEmpty())
                            order.getPaymentOrderMethod().getTransactionInformation().put("amount", payment.getTransactions().get(0).getAmount());
                        order.getPaymentOrderMethod().setPaymentId(payment.getId());
                        order.getPaymentOrderMethod().setPaymentToken((links.getHref().split(PATTERN)[1]));
                        order.getPaymentOrderMethod().getTransactionInformation().put("fullPayment", false);
                        orderRepository.save(order);
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
                Optional<Order> order = orderRepository.findOrderByPaymentOrderMethod_PaymentTokenAndStatusOrder(paymentToken, Constant.ORDER_PROCESS);
                if (order.isPresent()) {
                    order.get().getPaymentOrderMethod().getTransactionInformation().put("payer", payment.getPayer().getPayerInfo());
                    order.get().getPaymentOrderMethod().getTransactionInformation().put("paymentMethod", payment.getPayer().getPaymentMethod());
                    order.get().getPaymentOrderMethod().getTransactionInformation().put("fullPayment", true);
                    order.get().setStatusOrder(Constant.ORDER_WAITING);
                    orderRepository.save(order.get());
                    orderSendMail.setOrderSuccess(order.get());
                    orderSendMail.setSendMailService(mailService);
                    taskScheduler.schedule(orderSendMail, new Date(System.currentTimeMillis())) ;
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
        Optional<Order> order = orderRepository.findOrderByPaymentOrderMethod_PaymentTokenAndStatusOrder(id, Constant.ORDER_PROCESS);
        if (order.isPresent()) {
            order.get().setStatusOrder(Constant.ORDER_CANCEL);
            orderRepository.save(order.get());
            String putQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
            String putSold =payUtils.putSold(order.get(),false);
            if (putQuantityProduct == null && putSold ==null) {
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
        double TotalMoneyVN= ExchangeMoneyUtils.exchange(order.getTotalPrice().add(new BigDecimal(order.getShipment().getServiceShipDetail().get("totalFeeShip").toString())));
       // Amount amount = new Amount(currency, String.format("%.2f", TotalMoneyVN));
        Amount amount = new Amount(currency, String.format(String.valueOf(TotalMoneyVN)));
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
