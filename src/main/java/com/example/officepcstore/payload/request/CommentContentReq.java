package com.example.officepcstore.payload.request;

import lombok.Data;



@Data
public class CommentContentReq {
   private String description;
   private String productBuyOrderId;
   private double vote;

}
