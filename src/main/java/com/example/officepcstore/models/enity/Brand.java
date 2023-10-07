package com.example.officepcstore.models.enity;

import com.example.officepcstore.models.enity.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "brands")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    private String id;

    private String name;
    private String imageBrand;
    private String state;
    @DocumentReference(lookup="{'brand':?#{#self._id} }", lazy = true)
    @JsonIgnore
    @Indexed
    private List<Product> dependentProducts;

    public Brand(String name, String imageBrand, String state) {
        this.name = name;
        this.imageBrand = imageBrand;
        this.state = state;
    }
}
