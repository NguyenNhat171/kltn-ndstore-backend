package com.example.officepcstore.controllers;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.payload.request.PayReq;
import com.example.officepcstore.service.payment.SelectPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final SelectPaymentService selectPaymentService;

    @PostMapping(path = "/{paymentType}/{orderId}")
    public ResponseEntity<?> checkout (@PathVariable("paymentType") String paymentType,
                                       @PathVariable("orderId") String orderId,
                                       @RequestBody @Valid PayReq req,
                                       HttpServletRequest request) {
        return selectPaymentService.initializationPayment(request, orderId, paymentType, req);
    }

    @GetMapping("/{paymentType}/success")
    public ResponseEntity<?> successPay(@RequestParam(value = "paymentId", required = false) String paymentId,
                                        @RequestParam(value = "PayerID", required = false) String payerId,
                                        @RequestParam(value = "vnp_ResponseCode", required = false) String responseCode,
                                        @RequestParam(value = "vnp_OrderInfo", required = false) String id,
                                        @PathVariable("paymentType") String paymentType,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        switch (paymentType) {
            case Constant.PAYBYPAYPAL:
                return selectPaymentService.makePayment(paymentId,payerId,null,null, request, response);
            case Constant.PAYBYVNPAY:
                return selectPaymentService.makePayment(null, null, responseCode,id, request, response);
            default:
                return selectPaymentService.makePayment(paymentId, null,null,null, request, response);
        }
    }

    @GetMapping("/{paymentType}/cancel")
    public ResponseEntity<?> cancelPay(@RequestParam(value = "paymentId", required = false) String paymentId,
                                       @RequestParam(value = "token", required = false) String token,
                                       @PathVariable("paymentType") String paymentType,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        if (Constant.PAYBYPAYPAL.equals(paymentType)) {
            return selectPaymentService.cancelPayment(token, null, request, response);
        } else {
            return selectPaymentService.cancelPayment(paymentId, null, request, response);
        }
    }
}
