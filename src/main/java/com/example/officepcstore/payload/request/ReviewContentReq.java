package com.example.officepcstore.payload.request;

import lombok.Data;



@Data
public class ReviewContentReq {
   private String description;
   private String productBuyId;
   private double vote;

}
