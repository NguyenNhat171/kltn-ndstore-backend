package com.example.officepcstore.models.enity;

import com.example.officepcstore.models.enity.product.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    private String id;
    private String review;
    private double rateProductQuality;
    @DocumentReference(lazy = true)
    @JsonIgnore
    @Indexed
    private User user;
    @DocumentReference(lazy = true)
    @JsonIgnore
    @Indexed
    private Product product;
    private String state;
    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime commentDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @LastModifiedDate
    LocalDateTime commentUpdateDate;

}
