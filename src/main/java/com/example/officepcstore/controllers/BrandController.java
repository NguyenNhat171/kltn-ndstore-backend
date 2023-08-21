package com.example.officepcstore.controllers;

import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.service.BrandService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BrandController {
    private final BrandService brandService;
    @GetMapping(path = "/brands")
    public ResponseEntity<ResponseObjectData> findAll()
    {
        return brandService.findAll();
    }
}
