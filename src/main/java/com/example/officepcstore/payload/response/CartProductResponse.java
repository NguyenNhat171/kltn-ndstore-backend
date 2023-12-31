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
public class CartProductResponse {
    private String itemCartId;
    private String productId;
    private long productStock;
    private String productName;
    private int discount;
    private List<ProductImage> image;
    private BigDecimal productPrice;
    private BigDecimal discountPrice;
    private long quantity;
    private BigDecimal originSubPrice;
    private BigDecimal subPrice;
  //  private boolean reviewed;
    private String productState;

    public CartProductResponse(String itemCartId, String productId, String productName, List<ProductImage> image, BigDecimal productPrice, long quantity, BigDecimal originSubPrice, String productState) {
        this.itemCartId = itemCartId;
        this.productId = productId;
        this.productName = productName;
        this.image = image;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.originSubPrice = originSubPrice;
        this.productState = productState;
    }
}
