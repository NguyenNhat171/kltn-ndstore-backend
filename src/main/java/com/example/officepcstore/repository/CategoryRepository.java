package com.example.officepcstore.repository;

import com.example.officepcstore.models.enity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends MongoRepository<Category, String> {
    List<Category> findAllByState(String state);
   Page<Category>findAllByState(String state, Pageable pageable);
    Optional<Category> findCategoryByIdAndState(String id, String state);
//    @Aggregation("{ $group: { _id : $state, count: { $sum: 1 } } }")
//    List<StateCountAggregate> countAllByState();
}
