package com.example.officepcstore.models.enity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

//@Document(collection = "categories")
@Document(collection = "Category")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Category {
    @Id
    private String id;
    @NotBlank
    @Indexed(unique = true)
    private String titleCategory;
    private String imageCategory;
    @NotBlank
    private String display;

    public Category(String titleCategory, String imageCategory, String display) {
        this.titleCategory = titleCategory;
        this.imageCategory = imageCategory;
        this.display = display;
    }
}
