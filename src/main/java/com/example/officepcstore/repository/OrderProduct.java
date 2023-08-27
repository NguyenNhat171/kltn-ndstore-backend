package com.example.officepcstore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderProduct extends MongoRepository<OrderProduct, String> {
}
