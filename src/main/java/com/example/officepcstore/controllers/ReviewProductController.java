package com.example.officepcstore.controllers;

import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.payload.request.ReviewContentReq;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.service.ReviewProductService;
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
public class ReviewProductController {
    private final ReviewProductService reviewProductService;
    private final JwtUtils userJwt;
    @PostMapping("/review/content/create")
    public ResponseEntity<?> createCommentByUser(HttpServletRequest request, @RequestBody ReviewContentReq req) {
        User userReview = userJwt.getUserFromJWT(userJwt.getJwtFromHeader(request));
        if (!userReview.getId().isEmpty())
        return reviewProductService.createReviewByUser(userReview.getId(),req);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "Not found User Token");
    }
    @GetMapping(path = "/review/list/product/{productId}")
    public ResponseEntity<?> getPageCommentInProductId(@PathVariable("productId") String productId,
                                              @PageableDefault(size = 10, sort = "reviewDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable){
        return reviewProductService.getAllReviewProduct(productId, pageable);
    }

}
