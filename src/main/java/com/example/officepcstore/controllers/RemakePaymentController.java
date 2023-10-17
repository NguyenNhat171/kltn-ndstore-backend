package com.example.officepcstore.controllers;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.payload.request.PayReq;
import com.example.officepcstore.service.payment.SelectPaymentService;
import com.example.officepcstore.service.remakepayment.PaymentType;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/api/checkout")
public class RemakePaymentController {
    private final PaymentType paymentTypeRemake;

    @PostMapping(path = "/remake/{paymentType}/{orderId}")
    public ResponseEntity<?> checkout (@PathVariable("paymentType") String paymentType,
                                       @PathVariable("orderId") String orderId,
                                       HttpServletRequest request) {
        return paymentTypeRemake.initializationPayment(request, orderId, paymentType);
    }

    @GetMapping("/remake/{paymentType}/success")
    public ResponseEntity<?> successPay(@RequestParam(value = "paymentId", required = false) String paymentId,
                                        @RequestParam(value = "PayerID", required = false) String payerId,
                                        @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
                                        @RequestParam(value = "vnp_OrderInfo", required = false) String id,
                                        @PathVariable("paymentType") String paymentType,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        switch (paymentType) {
            case Constant.PAY_BY_PAYPAL:
                return paymentTypeRemake.makePayment(paymentId,payerId,null,null, request, response);
            case Constant.PAY_BY_VNPAY:
                return paymentTypeRemake.makePayment(null, null, responseCode,id, request, response);
            default:
                return paymentTypeRemake.makePayment(paymentId, null,null,null, request, response);
        }
    }

    @GetMapping("/remake/{paymentType}/cancel")
    public ResponseEntity<?> cancelPay(@RequestParam(value = "paymentId", required = false) String paymentId,
                                       @RequestParam(value = "token", required = false) String token,
                                       @PathVariable("paymentType") String paymentType,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        if (Constant.PAY_BY_PAYPAL.equals(paymentType)) {
            return paymentTypeRemake.cancelPayment(token, null, request, response);
        } else {
            return paymentTypeRemake.cancelPayment(paymentId, null, request, response);
        }
    }
}
