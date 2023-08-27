package com.example.officepcstore.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductResponse {
    private String itemId;
    private String productId;
    private String name;
    private int discount;
    private String image;
    private BigDecimal price;
    private long quantity;
   // private long stock;
    private BigDecimal subPrice;
  //  private boolean reviewed;
}
