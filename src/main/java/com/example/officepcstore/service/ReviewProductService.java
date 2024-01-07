package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.map.ReviewProductMapping;
import com.example.officepcstore.models.enity.ReviewProduct;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.ReviewContentReq;
import com.example.officepcstore.payload.response.ReviewProductResponse;
import com.example.officepcstore.repository.*;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewProductService {


    private final ReviewProductRepository reviewProductRepository;
    private final UserRepository userRepository;
   private final ProductRepository productRepository;
    private final ReviewProductMapping reviewProductMapping;
//    private final OrderProductRepository orderProductRepository;
//    private final OrderRepository orderRepository;



    public ResponseEntity<?> getAllReviewProduct(String id, Pageable pageable) {
        Page<ReviewProduct> reviews = reviewProductRepository.findAllByProductReview_Id(new ObjectId(id), pageable);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "No review this yet  ", ""));
        } else {
            List<ReviewProductResponse> reviewList = reviews.getContent().stream().map(reviewProductMapping::getReviewResponse).collect(Collectors.toList());
            Map<String, Object> reviewRes = new HashMap<>();
            reviewRes.put("numberOfReview", reviews.getTotalElements());
            reviewRes.put("allPage", reviews.getTotalPages());
            reviewRes.put("allReview", reviewList);
            if (reviews.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "No review this product yet  ", ""));
            else
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "All Review Product success ", reviewRes));
        }
    }



    public ResponseEntity<?> createReviewByUser(String customerId, ReviewContentReq reviewContentReq)
    {
        Optional<User> user = userRepository.findUserByIdAndStatusUser(customerId, Constant.USER_ACTIVE);
        if(user.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not found user "+customerId, ""));
        Optional<ReviewProduct> checkComment = reviewProductRepository.findReviewProductByUserReview_IdAndProductReview_Id(
                new ObjectId(customerId), new ObjectId(reviewContentReq.getProductBuyId()));
//        Optional<OrderedProduct> checkOrderedProduct=orderProductRepository.findOrderedProductByOrderProduct_Id(new ObjectId(commentContentReq.getProductBuyOrderId()));
     Optional<Product> checkProduct = productRepository.findProductByIdAndState(reviewContentReq.getProductBuyId(),Constant.ENABLE);
//        Optional<Order> checkOrder=orderRepository.findOrderByOrderedProducts_Id(new ObjectId(commentContentReq.getProductBuyOrderId()));
        if(checkComment.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "You have review this product ", ""));
        }
        else {
                ReviewProduct createReviewProduct =
                        new ReviewProduct(reviewContentReq.getDescription(), reviewContentReq.getVote(), user.get(), checkProduct.get(), LocalDateTime.now());
                reviewProductRepository.save(createReviewProduct);
            double voteReviewProduct = (checkProduct.get().getRate() + reviewContentReq.getVote())/ checkProduct.get().getNumberProductVote();
            checkProduct.get().setRate( voteReviewProduct);
            productRepository.save(checkProduct.get());
                return    ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Create user review complete ", createReviewProduct));

            }

    }


}
