package com.example.officepcstore.payload.request;

import lombok.Data;



@Data
public class CommentContentReq {
   private String description;
   private String productBuyId;
   private double vote;

}
