package com.example.officepcstore.models.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//@Document(collection = "brands")
@Document(collection = "Brand")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    private String id;

    private String name;
    private String imageBrand;
    private String display;

    public Brand(String name, String imageBrand, String display) {
        this.name = name;
        this.imageBrand = imageBrand;
        this.display = display;
    }
}
