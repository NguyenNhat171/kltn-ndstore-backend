package com.example.officepcstore.utils;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.repository.OrderProductRepository;
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
    private final OrderProductRepository orderProductRepository;

    @Synchronized
    @Transactional
    public String checkStockAndQuantityToUpdateProduct(Order order, boolean checkPayment) {
        order.getOrderDetails().forEach(item -> {
                if (checkPayment) {
                    if ( item.getOrderProduct().getStock() < item.getQuantity()) {
                        order.setStatusOrder(Constant.ORDER_CART);
                        orderRepository.save(order);
                        throw new AppException(HttpStatus.CONFLICT.value(),
                                "Quantity order this product exceeds stock:" + item.getOrderProduct().getName()+":"+item.getOrderProduct().getId()
                                        + ":" + item.getOrderProduct().getStock());
                    } else item.getOrderProduct().setStock(item.getOrderProduct().getStock() - item.getQuantity());
                } else item.getOrderProduct().setStock(item.getOrderProduct().getStock() + item.getQuantity());
            try {
                productRepository.save(item.getOrderProduct());
            } catch (MongoWriteException e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Update product quantity fail");
            }
        });
        return null;
    }


    public String compareStockAndProduct(Order order, boolean checkPayment) {
        order.getOrderDetails().forEach(item -> {
            if (checkPayment) {
                if ( item.getOrderProduct().getStock() < item.getQuantity()) {
                    order.setStatusOrder(Constant.ORDER_CART);
                    orderRepository.save(order);
                    throw new AppException(HttpStatus.CONFLICT.value(),
                            "Quantity order this product exceeds stock:" + item.getOrderProduct().getName()+":"+item.getOrderProduct().getId()
                                    + ":" + item.getOrderProduct().getStock());
                }
            }
        });
        return null;
    }

    @Synchronized
    @Transactional
    public String putSold(Order order, boolean checkPayment) {
        order.getOrderDetails().forEach(item -> {
            if (checkPayment) {
            item.getOrderProduct().setSold(item.getOrderProduct().getSold() + item.getQuantity());
            } else item.getOrderProduct().setSold(item.getOrderProduct().getSold() - item.getQuantity());
            try {
                productRepository.save(item.getOrderProduct());
            } catch (MongoWriteException e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Update sold fail");
            }
        });
        return null;
    }

    @Synchronized
    @Transactional
    public void updateProductPriceOrder(Order order) {
        order.getOrderDetails().forEach(item -> {
                item.setProductOrderPrice(item.getOrderProduct().getReducedPrice());
            try {
                orderProductRepository.save(item);
            } catch (MongoWriteException e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Update price fail");
            }
        });
    }

}
