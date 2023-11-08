package com.example.officepcstore.map;

import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.OrderDetail;
import com.example.officepcstore.payload.response.CartProductResponse;
import com.example.officepcstore.payload.response.CartResponse;
import com.example.officepcstore.payload.response.ItemOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;
@Service
@Slf4j
public class CartMap {
    public CartResponse getProductCartRes (Order order) { // toCartRes
        CartResponse res = new CartResponse(order.getId(), order.getTotalProduct(), order.getTotalPrice(), order.getStatusOrder());
        res.setItems(order.getOrderDetails().stream().map(CartMap::toCartProductRes).collect(Collectors.toList()));
        return res;
    }

    public static CartProductResponse toCartProductRes(OrderDetail product) { //toCartItemRes
        BigDecimal price = product.getPrice();
        try {
            return new CartProductResponse(product.getId(), product.getOrderProduct().getId(),product.getOrderProduct().getStock(),product.getOrderProduct().getName(),
                    product.getOrderProduct().getDiscount(),
                    product.getOrderProduct().getProductImageList(),  product.getOrderProduct().getPrice(),product.getOrderProduct().getReducedPrice(),
                    product.getQuantity(),price,product.getSubProductPrice(),product.getOrderProduct().getState());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Cant get product cart");
        }
    }

    public static ItemOrderResponse toCartProductOrderRes(OrderDetail product) {
        try {
            return new ItemOrderResponse(product.getId(),product.getOrderProduct().getId(),product.getOrderProduct().getName(),
                    product.getOrderProduct().getProductImageList(),product.getProductOrderPrice(), product.getQuantity(),product.getSubProductOrderPrice(),
                    product.getOrderProduct().getState()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Cant get product cart");
        }
    }


}
