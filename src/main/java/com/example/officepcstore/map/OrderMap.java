package com.example.officepcstore.map;

import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.response.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderMap {
    //toOrderRes
    public OrderResponse getOrderRes (Order order) {
        Object orderDate = order.getLastModifiedDate();
        if (order.getPaymentInformation().getPaymentInfo().get("orderDate") != null)
            orderDate = order.getPaymentInformation().getPaymentInfo().get("orderDate");
        return new OrderResponse(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotalProduct(), order.getTotalPrice(), order.getState(), orderDate);
    }
//toOrderDetailRes
    public OrderResponse getOrderDetailRes (Order order) {
        OrderResponse orderRes =  new OrderResponse(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotalProduct(), order.getTotalPrice(), order.getState(), order.getLastModifiedDate());
        if (order.getPaymentInformation().getPaymentInfo().get("orderDate") != null) orderRes.setCreatedDate(order.getPaymentInformation().getPaymentInfo().get("orderDate"));
        orderRes.setItems(order.getItems().stream().map(CartMap::toCartProductRes).collect(Collectors.toList()));
        orderRes.setPaymentType(order.getPaymentInformation().getPaymentType());
        orderRes.setPaymentInfo(order.getPaymentInformation().getPaymentInfo());
        orderRes.setShippingDetail(order.getShippingDetail());
        return orderRes;
    }
}
