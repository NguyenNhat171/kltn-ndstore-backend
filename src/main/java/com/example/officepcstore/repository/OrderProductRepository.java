package com.example.officepcstore.repository;


import com.example.officepcstore.models.enity.OrderDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderProductRepository extends MongoRepository<OrderDetail, String> {
//    Optional<OrderDetail>findOrderedProductByOrderProduct_Id(ObjectId productId);
}
