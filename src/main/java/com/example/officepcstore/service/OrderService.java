package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.OrderMap;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.CreateShipReq;
import com.example.officepcstore.payload.response.OrderResponse;
import com.example.officepcstore.repository.OrderRepository;
import com.example.officepcstore.utils.PayUtils;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.cloudinary.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMap orderMap;
    private final PayUtils payUtils;
    private final LogisticService logisticService;


    public ResponseEntity<?> findAll(String state, Pageable pageable) {
        Page<Order> orders;
        if (state.isBlank()) orders = orderRepository.findAll(pageable);
        else orders = orderRepository.findAllByState(state, pageable);
        if (orders.isEmpty()) throw new NotFoundException("Can not found any orders");
        List<OrderResponse> resList = orders.stream().map(orderMap::getOrderRes).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", orders.getTotalElements());
        resp.put("totalPage", orders.getTotalPages());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Get orders success", resp));
    }


    public ResponseEntity<?> findOrderById(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            OrderResponse orderResponse = orderMap.getOrderDetailRes(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", orderResponse));
        }
        else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found order with id"+id, ""));
    }


    public ResponseEntity<?> findOrderByUserId(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            OrderResponse orderResponse = orderMap.getOrderDetailRes(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", orderResponse));
        }
        else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found order with id"+ id, ""));
    }


    public ResponseEntity<?> cancelOrder(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            if (order.get().getState().equals(Constant.ORDER_STATE_PENDING) ||
                    order.get().getState().equals(Constant.ORDER_STATE_PROCESS)) {
                String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
                order.get().setState(Constant.ORDER_STATE_CANCEL);
                orderRepository.save(order.get());
                if (checkUpdateQuantityProduct == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObjectData(true, "Cancel order successfully", ""));
                }
            } else throw new AppException(HttpStatus.BAD_REQUEST.value(),
                    "You cannot cancel while the order is still processing!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found order with id"+ id, ""));
    }


    public ResponseEntity<?> createShip(CreateShipReq req, String orderId) {
        Optional<Order> order = orderRepository.findOrderByIdAndState(orderId, Constant.ORDER_STATE_PREPARE);
        if (order.isPresent()) {
            order.get().setState(Constant.ORDER_STATE_SHIPPING);
            HttpResponse<?> response = logisticService.create(req, order.get());
            JSONObject objectRes = new JSONObject(response.body().toString()).getJSONObject("data");
            order.get().getShippingDetail().getShipInfo().put("orderCode", objectRes.getString("order_code"));
            order.get().getShippingDetail().getShipInfo().put("fee", objectRes.getLong("total_fee"));
            order.get().getShippingDetail().getShipInfo().put("expectedDeliveryTime", objectRes.getString("expected_delivery_time"));
            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Create shipping complete", order.get().getShippingDetail().getShipInfo()));
        } else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found order with id"+ orderId, ""));
    }


    public ResponseEntity<?> changeStateShip(String state, String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            if (Constant.ORDER_STATE_DELIVERED.equals(state)) {
                if (order.get().getState().equals(Constant.ORDER_STATE_SHIPPING)) {
                    order.get().setState(Constant.ORDER_STATE_DELIVERED);
                    order.get().getShippingDetail().getShipInfo().put("deliveredAt", LocalDateTime.now(Clock.systemUTC()));
                } else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Order have not been delivering");
            } else {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid state");
            }
            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Change state order", state));
        }else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found order with id"+ orderId, ""));
    }


    public ResponseEntity<?> changeStateDone(String state, String orderId, String userId) {
        Optional<Order> order = orderRepository.findOrderByIdAndUser_Id(orderId, new ObjectId(userId));
        if (order.isPresent()) {
            if (Constant.ORDER_STATE_COMPLETE.equals(state)) {
                if (order.get().getState().equals(Constant.ORDER_STATE_DELIVERED)){
                    order.get().setState(Constant.ORDER_STATE_COMPLETE);
                    order.get().getPaymentInformation().getPaymentInfo().put("isPaid", true);
                }
                else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Order have not been delivered");
            } else {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid state");
            }
            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Finish order successfully", state));
        } else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found order with id"+ orderId, ""));
    }
}
