package com.example.officepcstore.service.payment;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.service.paymentconfig.VnpayConfig;
import com.example.officepcstore.utils.CheckTimePayment;
import com.example.officepcstore.utils.PayUtils;
import com.example.officepcstore.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@Service
public class VnpayService extends PaymentSteps{
    private final OrderRepository orderRepository;
    private final PayUtils payUtils;
    private final CheckTimePayment checkTimePayment;
    private final TaskScheduler taskScheduler;

    @SneakyThrows
    @Override
    public ResponseEntity<?>  initializationPayment(HttpServletRequest request, Order order) {
        order.setState(Constant.ORDER_PROCESS);
        order.getPaymentInformation().getPaymentInfo().put("isPaid", false);
        orderRepository.save(order);
        Map<String, Object> vnp_Params = mapVnPayParam(order, request);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName) + "";
            if (!fieldValue.isBlank() && fieldValue.length() > 0) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += VnpayConfig.vnp_SecureHash + vnp_SecureHash;
        String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;
        String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order, true);
        if (checkUpdateQuantityProduct == null) {
            checkTimePayment.setOrderId(order.getId());
            checkTimePayment.setOrderRepository(orderRepository);
           checkTimePayment.setPayUtils(payUtils);
            taskScheduler.schedule( checkTimePayment, new Date(System.currentTimeMillis() + Constant.PAYMENT_TIMEOUT)) ;
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Payment Complete", paymentUrl));
        } else throw new AppException(HttpStatus.CONFLICT.value(), "Quantity exceeds the available stock!");
    }


    @Override
    @SneakyThrows
    public ResponseEntity<?> makePayment(String paymentId, String payerId, String responseCode, String id, HttpServletRequest request, HttpServletResponse response) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty() || !order.get().getState().equals(Constant.ORDER_PROCESS)) {
            response.sendRedirect(SelectPaymentService.URL_PAYMENT + "false&cancel=false");
            throw new NotFoundException("Can not found order with id: " + id);
        }
        if (responseCode.equals(VnpayConfig.responseSuccessCode)) {
            order.get().getPaymentInformation().getPaymentInfo().put("amount", request.getParameter(VnpayConfig.vnp_Amount));
            order.get().getPaymentInformation().getPaymentInfo().put("bankCode", request.getParameter("vnp_BankCode"));
            order.get().getPaymentInformation().getPaymentInfo().put("transactionNo", request.getParameter("vnp_TransactionNo"));
            order.get().getPaymentInformation().getPaymentInfo().put("isPaid", true);
            order.get().setState(Constant.ORDER_PREPARE);
            orderRepository.save(order.get());
            response.sendRedirect(SelectPaymentService.URL_PAYMENT + "true&cancel=false");
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Payment Completed", "")
            );
        } else {
            order.get().setState(Constant.ORDER_CANCEL);
            orderRepository.save(order.get());
            String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
            if (responseCode.equals(VnpayConfig.responseCancelCode) && checkUpdateQuantityProduct == null) {
                response.sendRedirect(SelectPaymentService.URL_PAYMENT + "true&cancel=true");
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Payment cancel complete", ""));
            } else response.sendRedirect(SelectPaymentService.URL_PAYMENT + "false&cancel=false");
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Failed when payment");
        }
    }

    @Override
    public ResponseEntity<?> cancelPayment(String id, String responseCode, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Payment cancel complete", ""));
    }

    public Map<String, Object> mapVnPayParam(Order order, HttpServletRequest request) {
        String vnp_IpAddr = VnpayConfig.getIpAddress(request);
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String total = String.valueOf((order.getTotalPrice().add(new BigDecimal(order.getShippingDetail().getShipInfo().get("fee").toString())))
                .multiply(BigDecimal.valueOf(100)));

        Map<String, Object> vnp_Params = new HashMap<>();
        vnp_Params.put(VnpayConfig.vnp_Version_k, VnpayConfig.vnp_Version);
        vnp_Params.put(VnpayConfig.vnp_Command_k, VnpayConfig.vnp_Command);
        vnp_Params.put(VnpayConfig.vnp_TmnCode_k, VnpayConfig.vnp_TmnCode);
        vnp_Params.put(VnpayConfig.vnp_CurrCode, VnpayConfig.vnp_currCode);
        vnp_Params.put(VnpayConfig.vnp_TxnRef_k, vnp_TxnRef);
        vnp_Params.put(VnpayConfig.vnp_OrderInfo_k, order.getId());
        vnp_Params.put(VnpayConfig.vnp_OrderType, VnpayConfig.vnp_orderType);
        vnp_Params.put(VnpayConfig.vnp_Locale, VnpayConfig.vn);
        vnp_Params.put(VnpayConfig.vnp_ReturnUrl, StringUtils.getBaseURL(request) + VnpayConfig.vnp_Returnurl);
        vnp_Params.put(VnpayConfig.vnp_IpAddr, vnp_IpAddr);
        vnp_Params.put(VnpayConfig.vnp_Amount, total);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(VnpayConfig.GMT));
        SimpleDateFormat formatter = new SimpleDateFormat(VnpayConfig.yyyyMMddHHmmss);
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put(VnpayConfig.vnp_CreateDate, vnp_CreateDate);
        cld.add(Calendar.MINUTE, 10);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put(VnpayConfig.vnp_ExpireDate, vnp_ExpireDate);

        String fullName = order.getUser().getName();
        if (fullName != null && !fullName.isEmpty()) {
            int idx = fullName.indexOf(' ');
            if (idx != -1) {
                String firstName = fullName.substring(0, idx);
                String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
                vnp_Params.put(VnpayConfig.vnp_Bill_FirstName, firstName);
                vnp_Params.put(VnpayConfig.vnp_Bill_LastName, lastName);
            } else {
                vnp_Params.put(VnpayConfig.vnp_Bill_FirstName, fullName);
                vnp_Params.put(VnpayConfig.vnp_Bill_LastName, fullName);
            }
        }
        return vnp_Params;
    }
}