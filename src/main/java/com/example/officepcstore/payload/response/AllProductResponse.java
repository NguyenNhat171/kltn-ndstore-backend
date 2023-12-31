package com.example.officepcstore.payload.response;

import com.example.officepcstore.models.enity.product.ProductImage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AllProductResponse { //ProductListRes
    private String id;
    private String name;
    private String description;
    private BigDecimal originPrice;
    private BigDecimal discountPrice;
    private int discount;
    private long stock;
    private long sold;
    private double rate;
    private int rateCount;
    private String categoryId;
    private String nameCategory;
    private String brandId;
    private String nameBrand;
    private String state;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime createdDate;
    private List<ProductImage> images;
    private List<Map<String, String>> productConfiguration;
}
