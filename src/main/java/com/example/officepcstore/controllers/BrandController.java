package com.example.officepcstore.controllers;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.service.BrandService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BrandController {
    private final BrandService brandService;
    @PostMapping(path = "/admin/manage/addbrands", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addBrand (@RequestParam(value = "name") String name,
                                       @RequestParam(value = "file",required = false) MultipartFile file){
        return brandService.addBrand(name, file);
    }
    @GetMapping(path = "/brands")
    public ResponseEntity<ResponseObjectData> findAll()
    {
        return brandService.findAll();
    }
    @GetMapping(path = "/admin/manage/brands")
    public ResponseEntity<?> findAll (@RequestParam(value = "state", defaultValue = "") String state){
        return brandService.findAll(state);
    }

    @GetMapping(path = "/brands/{id}")
    public ResponseEntity<?> findBrandById (@PathVariable("id") String id){
        return brandService.findBrandById(id);
    }



    @PostMapping(path = "/admin/manage/updatebrands/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBrand (@PathVariable("id") String id,
                                          @RequestParam("name") String name,
                                          @RequestParam("state") String state,
                                          @RequestParam(value = "file",required = false) MultipartFile file) {
        if (name == null || name.isBlank()) throw new AppException(HttpStatus.BAD_REQUEST.value(), "Name is required");
        if (state == null || state.isBlank() || (!state.equalsIgnoreCase(Constant.ENABLE) &&
                !state.equalsIgnoreCase(Constant.DISABLE)))
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "State is invalid");
        return brandService.updateBrand(id, name, state, file);
    }

    @DeleteMapping(path = "/admin/manage/blockbrands/{id}")
    public ResponseEntity<?> blcokBrand (@PathVariable("id") String id){
        return brandService.blockBrand(id);
    }
}
