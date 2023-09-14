package com.example.officepcstore.models.enity;

import com.example.officepcstore.models.enity.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128;

@Document(collection = "ordered_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderedProduct {
    @Id
    private String id;
    @DocumentReference
    @Indexed
    private Product orderProduct;
    @NotNull
    private long quantity;
    @DocumentReference(lazy = true)
    @JsonIgnore
    @Indexed
    private Order order;
    @Field(targetType = DECIMAL128)
    private BigDecimal price = BigDecimal.ZERO;
    private boolean reviewed = false;
    @Transient
    private BigDecimal subProductPrice = BigDecimal.ZERO;

    public BigDecimal getPrice(){
        BigDecimal originPrice =orderProduct.getPrice().multiply(BigDecimal.valueOf(quantity));
        return originPrice;
    }



    public BigDecimal getSubProductPrice() {
            BigDecimal originPrice = (orderProduct.getPrice().multiply(BigDecimal.valueOf(quantity)));
            String discountString = originPrice.multiply(BigDecimal.valueOf((double) (100- orderProduct.getDiscount())/100))
                    .stripTrailingZeros().toPlainString();
            return new BigDecimal(discountString);
    }


//    public OrderedProduct(Product orderedProduct, long quantity, Order order) {
//        this.orderedProduct = orderedProduct;
//        this.quantity = quantity;
//        this.order = order;
//    }

    public OrderedProduct(Product orderProduct, long quantity, Order order) {
        this.orderProduct = orderProduct;
        this.quantity = quantity;
        this.order = order;
    }
}
