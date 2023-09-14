package com.example.officepcstore.repository;


import com.example.officepcstore.models.enity.OrderedProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderProductRepository extends MongoRepository<OrderedProduct, String> {
}
