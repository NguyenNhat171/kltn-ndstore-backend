package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.OrderMap;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.payload.ResponseObjectData;
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



    public ResponseEntity<?> findAll(String state, Pageable pageable) {
        Page<Order> orders;
        if (state.isBlank()) orders = orderRepository.findAllByStatusOrderNoCart(pageable);
        else orders = orderRepository.findAllByStatusOrder(state, pageable);
        if (orders.isEmpty()) ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found any order", ""));
        List<OrderResponse> resList = orders.stream().map(orderMap::getOrderDetailResponse).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", orders.getTotalElements());
        resp.put("totalPage", orders.getTotalPages());
        if(resList.size()>0){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", resp));
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found any order", ""));
    }


    public ResponseEntity<?> findOrderById(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            OrderResponse orderResponse = orderMap.getOrderDetailResponse(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", orderResponse));
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Can not found order with id"+id, ""));
    }


    public ResponseEntity<?> findOrderDetailByUserId(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            OrderResponse orderResponse = orderMap.getOrderDetailResponse(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", orderResponse));
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Can not found order with id"+ id, ""));
    }

    public ResponseEntity<?> findAllOrderByUserId(String userId, String state,Pageable pageable) {
//        Page<Order> orders = orderRepository.findOrderByUser_Id(new ObjectId(userId), pageable);
        Page<Order> listOrder;
        if (state.isBlank())
            listOrder = orderRepository.findOrdersByUser_IdAndStatusOrderNot(new ObjectId(userId), Constant.ORDER_CART,pageable);
        else
            listOrder= orderRepository.findOrdersByUser_IdAndStatusOrder(new ObjectId(userId), state,pageable);
        if (listOrder.isEmpty()) ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found any order", ""));
        List<OrderResponse> resList = listOrder.stream().map(orderMap::getOrderDetailResponse).collect(Collectors.toList());
        Map<String, Object> orderResp = new HashMap<>();
        orderResp.put("totalPage", listOrder.getTotalPages());
        orderResp.put("totalOrder",listOrder.getTotalElements());
        orderResp.put("listOrder", resList);
        if(resList.size()>0){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success", orderResp));
        }
       else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found any order",""));
    }
//    public ResponseEntity<?> findAllNoCart( Pageable pageable) {
//        Page<Order> orders = orderRepository.findAllByStatusOrderNoCart(pageable);
//        if (orders.isEmpty())
//            throw new NotFoundException("Can not found any orders");
//        List<OrderResponse> resList = orders.stream().map(orderMap::getOrderRes).collect(Collectors.toList());
//        Map<String, Object> resp = new HashMap<>();
//        resp.put("list", resList);
//        resp.put("totalQuantity", orders.getTotalElements());
//        resp.put("totalPage", orders.getTotalPages());
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObjectData(true, "Get orders success", resp));
//    }
    public ResponseEntity<?> cancelOrder(String id, String userId) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent() && order.get().getUser().getId().equals(userId)) {
            if (order.get().getStatusOrder().equals(Constant.ORDER_WAITING) || order.get().getStatusOrder().equals(Constant.ORDER_PROCESS)) {
                String checkUpdateQuantityProduct = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
                String checkUpdateSold =payUtils.putSold(order.get(),false);
                order.get().setLastUpdateStateDate(LocalDateTime.now());
                order.get().setStatusOrder(Constant.ORDER_CANCEL);
                orderRepository.save(order.get());
                if (checkUpdateQuantityProduct == null && checkUpdateSold == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObjectData(true, "Cancel order successfully", ""));
                }
            } else ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Cancel order failed", ""));;
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found order with id"+ id, ""));
    }

    public ResponseEntity<?> setCancelOrderByAdmin(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
                String updateQuantity = payUtils.checkStockAndQuantityToUpdateProduct(order.get(), false);
                String updateSold = payUtils.putSold(order.get(), false);
                order.get().setLastUpdateStateDate(LocalDateTime.now());
                order.get().setStatusOrder(Constant.ORDER_CANCEL);
                orderRepository.save(order.get());
                if (updateQuantity == null && updateSold == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObjectData(true, "Cancel order successfully", ""));
                }
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found order with id" + id, ""));
    }
    public ResponseEntity<?> setStateDeliveryOrder(String estimatedTimeDelivery , String orderId) {
        //Optional<Order> order = orderRepository.findById(orderId);
        Optional<Order> order = orderRepository.findOrderByIdAndStatusOrder(orderId,Constant.ORDER_WAITING);
        if(order.isPresent())
        {
                order.get().setStatusOrder(Constant.ORDER_PROCESS_DELIVERY);
            order.get().getShippingDetail().getServiceShipDetail().put("estimatedTime", estimatedTimeDelivery);
            orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Change state delivery complete", order.get().getShippingDetail().getServiceShipDetail()));
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found order with id"+ orderId, ""));
    }


    public ResponseEntity<?> setStateConfirmDelivery(String orderId) {
      //  Optional<Order> order = orderRepository.findById(orderId);
        Optional<Order> order = orderRepository.findOrderByIdAndStatusOrder(orderId,Constant.ORDER_PROCESS_DELIVERY);
        if (order.isPresent()) {
                    order.get().setStatusOrder(Constant.ORDER_SUCCESS);
                    order.get().setLastUpdateStateDate(LocalDateTime.now());
                    order.get().getPaymentInformation().getPayDetails().put("fullPayment", true);
                    orderRepository.save(order.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Change state order", " "));
        }else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found order with id"+ orderId, ""));
    }

//    public ResponseEntity<?> setStateProcessDelivery(String orderId) {
//        Optional<Order> order = orderRepository.findById(orderId);
//        if (order.isPresent()) {
//            if (order.get().getStatusOrder().equals(Constant.ORDER_SHIPPING)) {
//                order.get().setStatusOrder(Constant.ORDER_PROCESS_DELIVERY);
//                order.get().getShippingDetail().getServiceShipDetail().put("getShippedAt", LocalDateTime.now(Clock.systemUTC()));
//            } else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Order have not been delivering");
//
//            orderRepository.save(order.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Change state order", " "));
//        }else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                new ResponseObjectData(false, "Not found order with id"+ orderId, ""));
//    }


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
