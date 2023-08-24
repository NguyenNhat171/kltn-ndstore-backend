package com.example.officepcstore.payload.request;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductReq {
    private String name;
    private String description;
    private BigDecimal price;
  //  @Range(min = 0, max = 100, message = "Invalid discount! Only from 0 to 100")
    private int discount;
    private String category;
    private String brand;
    private String state;
}
