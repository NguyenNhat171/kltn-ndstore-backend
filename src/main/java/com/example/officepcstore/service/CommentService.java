package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.CommentMap;
import com.example.officepcstore.models.enity.CommentProduct;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.OrderedProduct;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.CommentContentReq;
import com.example.officepcstore.payload.response.CommentResponse;
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
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CommentMap commentMap;
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;

    public ResponseEntity<?> getAllCommentProduct(String id, Pageable pageable) {
        Page<CommentProduct> reviews = commentRepository.findAllByProductComment_Id(new ObjectId(id), pageable);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "No comments yet  ", ""));
        } else {
            List<CommentResponse> commentList = reviews.getContent().stream().map(commentMap::getCommentResponse).collect(Collectors.toList());
            Map<String, Object> commentRes = new HashMap<>();
            commentRes.put("numberOfComments", reviews.getTotalElements());
            commentRes.put("allPage", reviews.getTotalPages());
            commentRes.put("allComment", commentList);
            if (reviews.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "No comments yet  ", ""));
            else
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "All Comment Product success ", commentRes));
        }
    }
    public ResponseEntity<?> createCommentByUser(String customerId, CommentContentReq commentContentReq)
    {
        Optional<User> user = userRepository.findUserByIdAndStatusUser(customerId, Constant.USER_ACTIVE);
        if(user.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not found user "+customerId, ""));
        Optional<CommentProduct> checkComment = commentRepository.findCommentProductByUserComment_IdAndProductComment_Id(
                new ObjectId(customerId), new ObjectId(commentContentReq.getProductBuyOrderId()));
        Optional<OrderedProduct> checkOrderedProduct=orderProductRepository.findOrderedProductByOrderProduct_Id(new ObjectId(commentContentReq.getProductBuyOrderId()));
        Optional<Product> checkProduct = productRepository.findProductByIdAndState(checkOrderedProduct.get().getOrderProduct().getId(),Constant.ENABLE);
        Optional<Order> checkOrder=orderRepository.findOrderByOrderedProducts_Id(new ObjectId(commentContentReq.getProductBuyOrderId()));
        if(checkComment.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "You have comment this product ", ""));
        }
        else {
            if (checkOrder.isPresent() && checkOrder.get().getStatusOrder().equals(Constant.ORDER_SUCCESS) && checkOrderedProduct.isPresent()) {
                CommentProduct createCommentProduct =
                        new CommentProduct(commentContentReq.getDescription(), commentContentReq.getVote(), user.get(), checkProduct.get(), LocalDateTime.now());
                commentRepository.save(createCommentProduct);
             return    ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Create comment complete ", createCommentProduct));

            }
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "An error occurred  ", ""));
        }
    }
}
