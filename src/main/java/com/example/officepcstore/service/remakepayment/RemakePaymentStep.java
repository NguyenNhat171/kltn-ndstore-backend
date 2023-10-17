package com.example.officepcstore.service.remakepayment;

import com.example.officepcstore.models.enity.Order;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class RemakePaymentStep{
public abstract ResponseEntity<?> initializationPayment(HttpServletRequest httpServletRequest, Order order);
public abstract ResponseEntity<?> makePayment(String paymentId, String payerId, String codeResponse, String id, HttpServletRequest request, HttpServletResponse httpServletResponse);
public abstract ResponseEntity<?> cancelPayment(String id, String codeResponse, HttpServletResponse httpServletResponse );
}
