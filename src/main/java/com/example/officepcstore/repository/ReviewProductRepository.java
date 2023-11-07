package com.example.officepcstore.repository;



import com.example.officepcstore.models.enity.ReviewProduct;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewProductRepository extends MongoRepository<ReviewProduct, String> {
    Page<ReviewProduct>findAllByProductReview_Id(ObjectId id, Pageable pageable);
    Optional<ReviewProduct>findReviewProductByUserReview_IdAndProductReview_Id(ObjectId userid, ObjectId productOrder);
}
