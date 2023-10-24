package com.example.officepcstore.controllers;

import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.payload.request.CommentContentReq;
import com.example.officepcstore.payload.request.ProductReq;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.service.CommentService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CommentProductController {
    private final CommentService commentService;
    private final JwtUtils userJwt;
    @PostMapping("/comment/content/create")
    public ResponseEntity<?> createCommentByUser(HttpServletRequest request, @RequestBody CommentContentReq req) {
        User userComment = userJwt.getUserFromJWT(userJwt.getJwtFromHeader(request));
        if (!userComment.getId().isEmpty())
        return commentService.createCommentByUser(userComment.getId(),req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "Not found User Token");
    }
    @GetMapping(path = "/comment/list/product/{productId}")
    public ResponseEntity<?> getPageCommentInProductId(@PathVariable("productId") String productId,
                                              @PageableDefault(size = 10, sort = "commentDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable){
        return commentService.getAllCommentProduct(productId, pageable);
    }

}
