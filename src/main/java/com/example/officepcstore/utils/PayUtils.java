package com.example.officepcstore.utils;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.repository.ProductRepository;
import com.mongodb.MongoWriteException;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PayUtils {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Synchronized
    @Transactional
    public String checkStockAndQuantityToUpdateProduct(Order order, boolean isPaid) {
        order.getItems().forEach(item -> {
                if (isPaid) {
                    if (  item.getItem().getStock() < item.getQuantity()) {
                        order.setState(Constant.ORDER_STATE_ENABLE);
                        orderRepository.save(order);
                        throw new AppException(HttpStatus.CONFLICT.value(),
                                "Quantity exceeds available stock this Product:" + item.getItem().getName()+":"+item.getItem().getId()
                                        + ":" + item.getItem().getStock());
                    } else item.getItem().setStock(item.getItem().getStock() - item.getQuantity());
                } else item.getItem().setStock(item.getItem().getStock() + item.getQuantity());
            try {
                productRepository.save(item.getItem());
            } catch (MongoWriteException e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Failed when update quantity");
            }
        });
        return null;
    }



}
