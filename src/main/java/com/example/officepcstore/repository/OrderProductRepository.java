package com.example.officepcstore.repository;


import com.example.officepcstore.models.enity.OrderedProduct;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderProductRepository extends MongoRepository<OrderedProduct, String> {
    Optional<OrderedProduct>findOrderedProductByOrderProduct_Id(ObjectId productId);
}
