package com.example.officepcstore.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentResponse {
    private String id;
    private String review;
    private double rate;
    private String userIdComment;
    private String userNameComment;
    private String  productIdComment;
    private String  productNameComment;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime commentCreateDate;
}
