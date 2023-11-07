package com.example.officepcstore.models.enity;

import com.example.officepcstore.models.enity.product.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Document(collection = "reviews_product")
@Data
@NoArgsConstructor
public class ReviewProduct {
    @Id
    private String id;
    private String reviewDescription;
    private double voteProduct;
    @DocumentReference(lazy = true)
    @JsonIgnore
    @Indexed
    private User userReview;
    @DocumentReference(lazy = true)
    @JsonIgnore
    @Indexed
    private Product productReview;
    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime reviewDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @LastModifiedDate
    LocalDateTime reviewUpdateDate;

    public ReviewProduct(String reviewDescription, double voteProduct, User userReview, Product productReview, LocalDateTime reviewDate) {
        this.reviewDescription = reviewDescription;
        this.voteProduct = voteProduct;
        this.userReview = userReview;
        this.productReview = productReview;
        this.reviewDate = reviewDate;
    }
}
