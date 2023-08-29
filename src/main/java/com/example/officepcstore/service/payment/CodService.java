package com.example.officepcstore.service.payment;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.utils.PayUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@Service
public class CodService extends PaymentSteps {
    private PayUtils payUtils;
    private final OrderRepository orderRepository;
    private final TaskScheduler taskScheduler;
    @Override
    @Transactional
    public ResponseEntity<?> initializationPayment(HttpServletRequest httpServletRequest, Order order)
    {
        if (order != null && order.getState().equals(Constant.ORDER_PROCESS)) {
            String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order, true);
            if (checkUpdateQuantityProduct == null) {
                order.setState(Constant.ORDER_PREPARE);
                order.getPaymentInformation().getPaymentInfo().put("isPaid", false);
                orderRepository.save(order);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, " Pay by COD successfully", ""));
            }
        } throw new NotFoundException("Can not found order with id: "+ Objects.requireNonNull(order).getId());
    }

    @Override
    public ResponseEntity<?> makePayment(String paymentId, String payerId, String responseCode, String id, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public ResponseEntity<?> cancelPayment(String id, String responseCode, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Payment cancel complete", ""));
    }
}
