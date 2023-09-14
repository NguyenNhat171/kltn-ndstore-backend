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
        if (order.getPaymentInformation().getPayDetails().get("invoiceDate") != null)
            orderDate = order.getPaymentInformation().getPayDetails().get("invoiceDate");
        return new OrderResponse(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotalProduct(), order.getTotalPrice(), order.getState(), orderDate);
    }
//toOrderDetailRes
    public OrderResponse getOrderDetailRes (Order order) {
        OrderResponse orderRes =  new OrderResponse(order.getId(), order.getUser().getId(), order.getUser().getName(),
                order.getTotalProduct(), order.getTotalPrice(), order.getState(), order.getLastModifiedDate());
        if (order.getPaymentInformation().getPayDetails().get("invoiceDate") != null)
            orderRes.setCreatedDate(order.getPaymentInformation().getPayDetails().get("invoiceDate"));
        orderRes.setItems(order.getOrderedProducts().stream().map(CartMap::toCartProductRes).collect(Collectors.toList()));
        orderRes.setPaymentType(order.getPaymentInformation().getPaymentType());
        orderRes.setPaymentInfo(order.getPaymentInformation().getPayDetails());
        orderRes.setShippingDetail(order.getShippingDetail());
        return orderRes;
    }
}
