package com.example.officepcstore.payload.response;

import com.example.officepcstore.models.enity.product.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemOrderResponse {
    private String itemCartId;
    private String productId;
    private String productName;
    private List<ProductImage> image;
    private BigDecimal productPrice;
    private long quantity;
    private BigDecimal subPrice;
    //  private boolean reviewed;
    private String productState;
}
