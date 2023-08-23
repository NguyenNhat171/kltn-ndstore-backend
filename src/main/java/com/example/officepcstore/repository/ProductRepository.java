package com.example.officepcstore.repository;

import com.example.officepcstore.models.enity.product.Product;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findProductByIdAndState(String id, String state);
    Page<Product> findAllByState(String state, Pageable pageable);
    Page<Product> findAllByCategory_IdOrBrand_IdAndState(ObjectId catId, ObjectId brandId, String state, Pageable pageable);

    @Query(value = "{ $or: [{'category' : {$in: ?0}},{'brand':{$in: ?1}}] ," +
            "    'state' : 'enable'}")
    Page<Product> findAllByCategoryOrBrand(List<ObjectId> catIds, List<ObjectId> brandIds, String state, Pageable pageable);
}
