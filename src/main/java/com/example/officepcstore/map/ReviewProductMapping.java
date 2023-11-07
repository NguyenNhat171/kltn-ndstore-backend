package com.example.officepcstore.map;


import com.example.officepcstore.models.enity.ReviewProduct;
import com.example.officepcstore.payload.response.ReviewProductResponse;
import org.springframework.stereotype.Service;

@Service
public class ReviewProductMapping {
    public ReviewProductResponse getReviewResponse(ReviewProduct reviewProduct)
    {
        return
                new ReviewProductResponse(reviewProduct.getId(), reviewProduct.getReviewDescription(), reviewProduct.getVoteProduct(), reviewProduct.getUserReview().getId(),reviewProduct.getUserReview().getName(),reviewProduct.getProductReview().getId(),reviewProduct.getProductReview().getName() ,reviewProduct.getReviewDate());
    }

}
