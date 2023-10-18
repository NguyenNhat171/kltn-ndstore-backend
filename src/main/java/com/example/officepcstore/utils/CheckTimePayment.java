package com.example.officepcstore.utils;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.repository.OrderRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@Component
@Slf4j
public class CheckTimePayment implements Runnable {
    private String orderId;
    private PayUtils payUtils;
    private OrderRepository orderRepository;

    @Override
    @Async
    @Transactional
    public void run() {
        log.info("Start time Payment!");
        if (!orderId.isBlank()) {
            Optional<Order> order = orderRepository.findOrderByIdAndStatusOrder(orderId, Constant.ORDER_PROCESS);
            if (order.isPresent()) {
                try {
                    if (new Date(System.currentTimeMillis() - Constant.PAYMENT_TIMEOUT).after(
                            (Date) order.get().getPaymentInformation().getPayDetails()
                                    .get("orderCreateTime"))) {
                        String check = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
                        String checkSold = payUtils.putSold(order.get(), false);
                        log.info("Back Stock" + (check == null) +"Back Sold"+(checkSold == null));
                        order.get().setStatusOrder(Constant.ORDER_CANCEL);
                        orderRepository.save(order.get());
                        log.info("Checking payment complete");
                    } else log.warn("Expiration time within");
                } catch (Exception e) {
                    log.error(e.getMessage());
                    log.error("order have not been save");
                }
            }
        } else log.error("Order id in checking payment timeout is blank!");
        log.info("Time out");
    }
}
