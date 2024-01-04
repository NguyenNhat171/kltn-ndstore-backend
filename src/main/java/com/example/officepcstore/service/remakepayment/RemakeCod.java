package com.example.officepcstore.service.remakepayment;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.service.MailService;
import com.example.officepcstore.service.OrderSendMail;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
@AllArgsConstructor
@Service
public class RemakeCod extends RemakePaymentStep{

    private final OrderRepository orderRepository;
    private final TaskScheduler taskScheduler;
    private final OrderSendMail orderSendMail;
    private final MailService mailService;

    @Override
    @Transactional
    public ResponseEntity<?> initializationPayment(HttpServletRequest httpServletRequest, Order order) {
        if (order != null && order.getStatusOrder().equals(Constant.ORDER_PROCESS)) {
                order.setStatusOrder(Constant.ORDER_WAITING);
                order.getPaymentOrderMethod().getTransactionInformation().put("fullPayment", false);
                orderRepository.save(order);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, " Pay by COD successfully", ""));
        }
        throw new NotFoundException("Not found order with id: " + Objects.requireNonNull(order).getId());
    }

    @Override
    public ResponseEntity<?> makePayment(String paymentId, String payerId, String responseCode, String id, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public ResponseEntity<?> cancelPayment(String id, String responseCode, HttpServletResponse response) {
      return null;
    }
}
