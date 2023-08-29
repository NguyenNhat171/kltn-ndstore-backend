package com.example.officepcstore.map;

import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.OrderProduct;
import com.example.officepcstore.models.enity.product.ProductImage;
import com.example.officepcstore.payload.response.CartProductResponse;
import com.example.officepcstore.payload.response.CartResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Slf4j
public class CartMap {
    public CartResponse getProductCartRes (Order order) { // toCartRes
        CartResponse res = new CartResponse(order.getId(), order.getTotalProduct(), order.getTotalPrice(), order.getState());
        res.setItems(order.getItems().stream().map(CartMap::toCartProductRes).collect(Collectors.toList()));
        return res;
    }

    public static CartProductResponse toCartProductRes(OrderProduct orderItem) { //toCartItemRes
        Optional<ProductImage> image = Optional.ofNullable(orderItem.getItem().getImages().get(0));
        BigDecimal price = orderItem.getPrice();

        try {
            return new CartProductResponse(orderItem.getId(), orderItem.getItem().getId(),orderItem.getItem().getName(),
                    orderItem.getItem().getDiscount(),
                    image.get().getUrl(), price,
                    orderItem.getQuantity(), orderItem.getSubPrice());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Cant get product cart");
        }
    }


}
