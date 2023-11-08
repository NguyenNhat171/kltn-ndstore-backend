package com.example.officepcstore.models.enity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    @DocumentReference(lazy = true)
    @JsonIgnore
    private User user;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'order':?#{#self._id} }", lazy = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
   private Shipment shipment = new Shipment();
    private PaymentOrderMethod paymentOrderMethod = new PaymentOrderMethod();
    @NotBlank(message = "State is required")
    private String statusOrder;
    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime invoiceDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @LastModifiedDate
    LocalDateTime lastUpdateStateDate;
    @Transient
    private long totalProduct = 0;
    @Transient
    private BigDecimal totalPrice;

    @Field(targetType = DECIMAL128)
    private BigDecimal totalPriceOrder;

    public long getTotalProduct() {
        return orderDetails.size();
    }

    public BigDecimal getTotalPrice() {
        totalPrice = orderDetails.stream().map(OrderDetail::getSubProductPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalPrice;
    }

    public Order(User user, String statusOrder) {
        this.user = user;
        this.statusOrder = statusOrder;
    }
}
