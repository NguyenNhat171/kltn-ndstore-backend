package com.example.officepcstore.service;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.CommentMap;
import com.example.officepcstore.models.enity.Comment;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.response.CommentResponse;
import com.example.officepcstore.repository.CommentRepository;
import com.example.officepcstore.repository.ProductRepository;
import com.example.officepcstore.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CommentMap commentMap;

    public ResponseEntity<?> getAllCommentInProduct(String productId, Pageable pageable) {
        Page<Comment> comments;
            comments  = commentRepository.findAllByProduct_IdAndState(new ObjectId(productId) , Constant.ENABLE, pageable);
        if (comments.isEmpty()) throw new NotFoundException("Can not found any review");
        List<CommentResponse> commentResponseList = comments.getContent().stream().map(commentMap::getCommentResponse).collect(Collectors.toList());
        Map<String, Object> dataRes = new HashMap<>();
        dataRes.put("listComment",  commentResponseList);
        dataRes.put("totalQuantity", comments.getTotalElements());
        dataRes.put("totalPage", comments.getTotalPages());
        if (comments.isEmpty()) throw new NotFoundException("Can not found any review");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(true, "Get review by product success ", dataRes));
    }
}
