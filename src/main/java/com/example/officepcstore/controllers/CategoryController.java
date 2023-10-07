package com.example.officepcstore.controllers;

import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.CategoryReq;
import com.example.officepcstore.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping(path = "/categories/user/get/all")
    public ResponseEntity<?> findAllCategoryUser(){
        return categoryService.findAllByUser();
    }

    @GetMapping(path = "/categories/get/details/{id}")
    public ResponseEntity<?> findCategoryById (@PathVariable("id") String id){
        return categoryService.findCategoryById(id);
    }


    @GetMapping(path = "/admin/manage/categories/get/list/all")
    public ResponseEntity<?> findAllByAdmin (@RequestParam(value = "state", defaultValue = "") String state,
                                             @PageableDefault(size = 15, sort = "name",
                                                     direction = Sort.Direction.ASC) @ParameterObject Pageable pageable)
    {
        return categoryService.findAllByAdmin(state,pageable);
    }
    @GetMapping(path = "/admin/manage/categories/get/details/{id}")
    public ResponseEntity<?> findCategoryByIdInAdmin (@PathVariable("id") String id){
        return categoryService.findCategoryByIdInAdmin(id);
    }


    @PostMapping(path = "/admin/manage/create/new/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCategory (@ModelAttribute @Valid CategoryReq req){
        return categoryService.addCategory(req);
    }

    @PutMapping(path = "/admin/manage/categories/update/details/{id}")
    public ResponseEntity<?> updateCategory (@PathVariable("id") String id,
                                             @RequestBody @Valid CategoryReq req){
        return categoryService.updateCategory(id, req);
    }

    @PostMapping(path = "/admin/manage/categories/update/new/image/{id}")
    public ResponseEntity<?> updateCategoryImage (@PathVariable("id") String id,
                                             @RequestParam(value = "file") MultipartFile file){
        return categoryService.updateCategoryImage(id, file);
    }

    @PutMapping(path = "/admin/manage/categories/change/disable/{id}")
    public ResponseEntity<?> changeStateDisableCategory (@PathVariable("id") String id){
        return categoryService.changeStateDisableCategory(id);
    }
    @PutMapping(path = "/admin/manage/categories/change/enable/{id}")
    public ResponseEntity<?> changeStateEnableCategory (@PathVariable("id") String id){
        return categoryService.changeStateEnableCategory(id);
    }
}
