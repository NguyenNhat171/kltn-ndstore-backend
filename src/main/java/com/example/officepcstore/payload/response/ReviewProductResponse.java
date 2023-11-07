package com.example.officepcstore.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ReviewProductResponse {
    private String id;
    private String reviewDescription;
    private double rate;
    private String userIdReview;
    private String userNameReview;
    private String  productIdReview;
    private String  productNameReview;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime commentCreateDate;
}
