package com.example.officepcstore.repository;


import com.example.officepcstore.models.enity.OrderProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderProductRepository extends MongoRepository<OrderProduct, String> {
}
