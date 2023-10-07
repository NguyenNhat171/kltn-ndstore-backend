package com.example.officepcstore.controllers;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.BrandReq;
import com.example.officepcstore.service.BrandService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BrandController {
    private final BrandService brandService;
    @PostMapping(path = "/admin/manage/brands/add/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addBrand (@RequestParam(value = "name") String name,
                                       @RequestParam(value = "file",required = false) MultipartFile file){
        return brandService.addBrand(name, file);
    }
    @GetMapping(path = "/brands/get/all")
    public ResponseEntity<ResponseObjectData> findAll()
    {
        return brandService.findAll();
    }
    @GetMapping(path = "/admin/manage/brands/get/list/all")
    public ResponseEntity<?> findAllByAdmin (@RequestParam(value = "state", defaultValue = "") String state,
                                            @PageableDefault(size = 15, sort = "name",
                                              direction = Sort.Direction.ASC) @ParameterObject Pageable pageable)
    {
        return brandService.findAllByAdmin(state,pageable);
    }



    @GetMapping(path = "/brands/get/detail/{id}")
    public ResponseEntity<?> findBrandByIdInUser (@PathVariable("id") String id){

        return brandService.findBrandByIdInUser(id);
    }
    @GetMapping(path = "/admin/manage/brands/detail/{id}")
    public ResponseEntity<?> findBrandByIdInAdmin (@PathVariable("id") String id){

        return brandService.findBrandByIdInAdmin(id);
    }



    @PutMapping(path = "/admin/manage/brands/update/detail/{id}")
    public ResponseEntity<?> updateBrand (@PathVariable("id") String id, @RequestBody @Valid BrandReq brandReq) {
        return brandService.updateDetailBrand(id, brandReq);
    }

    @PostMapping(path = "/admin/manage/brand/update/new/image/{id}")
    public ResponseEntity<?> updateBrandImage (@PathVariable("id") String id,
                                                  @RequestParam(value = "file") MultipartFile file){
        return brandService.updateBrandImage(id, file);
    }

    @PutMapping(path = "/admin/manage/brands/disable/{id}")
    public ResponseEntity<?> changStateDisableBrand (@PathVariable("id") String id){
        return brandService.changeStateDisableBrand(id);
    }
    @PutMapping(path = "/admin/manage/brands/enable/{id}")
    public ResponseEntity<?> changStateEnableBrand (@PathVariable("id") String id){
        return brandService.changeStateEnableBrand(id);
    }
}
