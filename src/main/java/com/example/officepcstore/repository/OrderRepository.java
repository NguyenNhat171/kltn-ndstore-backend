package com.example.officepcstore.repository;

import com.example.officepcstore.models.enity.Order;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findOrderByUser_Id(ObjectId userId);

   Optional<Order> findOrderByUser_IdAndStatusOrder(ObjectId userId, String state);
//  Optional<Order> findOrderByIdAndUser_Id(String orderId, ObjectId userId);
    Optional<Order> findOrderByPaymentOrderMethod_PaymentTokenAndStatusOrder(String token, String state);
   Optional<Order> findOrderByIdAndStatusOrder(String orderId, String state);
    Page<Order> findAllByStatusOrder(String state, Pageable pageable);
//    Page<Order> findOrderByUser_Id(ObjectId userId, Pageable pageable);
    Page<Order> findOrdersByUser_IdAndStatusOrderNot(ObjectId userId,String state ,Pageable pageable);
    Page<Order> findOrdersByUser_IdAndStatusOrder(ObjectId userId,String state ,Pageable pageable);

  Page<Order> findAllByInvoiceDateBetweenAndStatusOrder(LocalDateTime from, LocalDateTime to, String state, Pageable pageable);
//    @Aggregation("{ $group: { _id : $state, count: { $sum: 1 } } }")
//    List<StateCountAggregate> countAllByState();

    @Query(value=" {statusOrder: {'$nin': ['cart']}}")
    Page<Order> findAllByStatusOrderNoCart( Pageable pageable);
  //  Page<Order>findOrdersByInvoiceDateBetween();
}
