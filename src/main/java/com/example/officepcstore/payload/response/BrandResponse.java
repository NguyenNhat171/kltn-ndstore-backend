package com.example.officepcstore.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandResponse {
    private String id;
    private String name;
    private String imageBrand;
    private String state;
}
