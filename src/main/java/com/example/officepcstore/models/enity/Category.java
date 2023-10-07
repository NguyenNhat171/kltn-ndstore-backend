package com.example.officepcstore.models.enity;


import com.example.officepcstore.config.Constant;
import com.example.officepcstore.models.enity.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Category {
    @Id
    private String id;

    @Indexed(unique = true)
    private String titleCategory;
    private String imageCategory;
    private String state;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'category':?#{#self._id} }", lazy = true)
    @JsonIgnore
    @Indexed
    private List<Product> dependentProducts;


    public Category(String titleCategory, String imageCategory, String state) {
        this.titleCategory = titleCategory;
        this.imageCategory = imageCategory;
        this.state = state;
    }
}
