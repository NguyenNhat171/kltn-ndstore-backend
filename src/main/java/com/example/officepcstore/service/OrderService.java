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
        else orders = orderRepository.findAllByStatusOrder(state, pageable);
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


    public ResponseEntity<?> findOrderDetailByUserId(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            OrderResponse orderResponse = orderMap.getOrderDetailRes(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", orderResponse));
        }
        else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found order with id"+ id, ""));
    }

    public ResponseEntity<?> findAllOrderByUserId(String userId, Pageable pageable) {
//        Page<Order> orders = orderRepository.findOrderByUser_Id(new ObjectId(userId), pageable);
        Page<Order> orders = orderRepository.findOrderByUser_IdAndStatusOrderNot(new ObjectId(userId), Constant.ORDER_CART,pageable);
        List<OrderResponse> resList = orders.stream().map(orderMap::getOrderDetailRes).collect(Collectors.toList());
        Map<String, Object> orderResp = new HashMap<>();
        orderResp.put("totalPage", orders.getTotalPages());
        orderResp.put("totalOrder", orders.getTotalElements());
        orderResp.put("listOrder", resList);
        if(resList.size()>0){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", orderResp));
        }
       else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found any order",orderResp));
    }
    public ResponseEntity<?> findAllNoCart( Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByStatusOrderNoCart(pageable);
        if (orders.isEmpty())
            throw new NotFoundException("Can not found any orders");
        List<OrderResponse> resList = orders.stream().map(orderMap::getOrderRes).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", orders.getTotalElements());
        resp.put("totalPage", orders.getTotalPages());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Get orders success", resp));
    }
    public ResponseEntity<?> cancelOrder(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            if (order.get().getStatusOrder().equals(Constant.ORDER_PAY_COD) || order.get().getStatusOrder().equals(Constant.ORDER_PAY_ONLINE)
                    || order.get().getStatusOrder().equals(Constant.ORDER_PROCESS)) {
                String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
                String checkUpdateSold =payUtils.updateSoldProduct(order.get(),false);
                order.get().setLastUpdateStateDate(LocalDateTime.now());
                order.get().setStatusOrder(Constant.ORDER_CANCEL);
                orderRepository.save(order.get());
                if (checkUpdateQuantityProduct == null && checkUpdateSold == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObjectData(true, "Cancel order successfully", ""));
                }
            } else throw new AppException(HttpStatus.BAD_REQUEST.value(),
                    "You cannot cancel while the order is still processing!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found order with id"+ id, ""));
    }


    public ResponseEntity<?> createShip(CreateShipReq req, String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent())
        {
            if(order.get().getStatusOrder().equals(Constant.ORDER_PAY_COD) || order.get().getStatusOrder().equals(Constant.ORDER_PAY_ONLINE))
                order.get().setStatusOrder(Constant.ORDER_SHIPPING);
            HttpResponse<?> response = logisticService.create(req, order.get());
            JSONObject objectRes = new JSONObject(response.body().toString()).getJSONObject("data");
            order.get().getShippingDetail().getServiceShipDetail().put("orderCode", objectRes.getString("order_code"));
            order.get().getShippingDetail().getServiceShipDetail().put("totalFeeShip", objectRes.getLong("total_fee"));
            order.get().getShippingDetail().getServiceShipDetail().put("estimatedTime", objectRes.getString("expected_delivery_time"));
            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Create shipping complete", order.get().getShippingDetail().getServiceShipDetail()));
        } else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found order with id"+ orderId, ""));
    }


    public ResponseEntity<?> setStateConfirmDelivery(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
                if (order.get().getStatusOrder().equals(Constant.ORDER_PROCESS_DELIVERY)) {
                    order.get().setStatusOrder(Constant.ORDER_COMPLETE);
                    order.get().setLastUpdateStateDate(LocalDateTime.now());
                    order.get().getPaymentInformation().getPayDetails().put("fullPayment", true);
                } else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Order have not been delivered");
            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Change state order", " "));
        }else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found order with id"+ orderId, ""));
    }

    public ResponseEntity<?> setStateProcessDelivery(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            if (order.get().getStatusOrder().equals(Constant.ORDER_SHIPPING)) {
                order.get().setStatusOrder(Constant.ORDER_PROCESS_DELIVERY);
                order.get().getShippingDetail().getServiceShipDetail().put("getShippedAt", LocalDateTime.now(Clock.systemUTC()));
            } else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Order have not been delivering");

            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Change state order", " "));
        }else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found order with id"+ orderId, ""));
    }


//    public ResponseEntity<?> changeStateDone(String orderId) {
//        Optional<Order> order = orderRepository.findById(orderId);
//        if (order.isPresent()) {
//          {
//                if (order.get().getState().equals(Constant.ORDER_CONFIRM_DELIVERED)){
//                    order.get().setState(Constant.ORDER_COMPLETE);
//                    order.get().getPaymentInformation().getPaymentInfo().put("isPaid", true);
//                }
//                else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Order have not been delivered");
//            }
//            orderRepository.save(order.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Finish order successfully"," "));
//        } else return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObjectData(false, "Can not found order with id"+ orderId, ""));
//    }
}
