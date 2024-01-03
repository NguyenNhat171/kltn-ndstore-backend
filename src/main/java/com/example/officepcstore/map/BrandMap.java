package com.example.officepcstore.map;


import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.payload.response.BrandResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrandMap {

    public BrandResponse getBrandResponse(Brand brand)
    {
        return
                new BrandResponse(brand.getId(), brand.getName(), brand.getImageBrand(),brand.getDisplay());
    }

}
