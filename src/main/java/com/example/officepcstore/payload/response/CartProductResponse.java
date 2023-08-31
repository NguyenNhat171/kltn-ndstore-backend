package com.example.officepcstore.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductResponse {
    private String itemCartId;
    private String productId;
    private long productStock;
    private String productName;
    private BigDecimal productPrice;
    private int discount;
    private String image;
    private BigDecimal originPrice;
    private long quantity;
    private BigDecimal subPrice;
  //  private boolean reviewed;
}
