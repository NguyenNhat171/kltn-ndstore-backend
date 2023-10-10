package com.example.officepcstore.controllers;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.payload.request.AddImageReq;

import com.example.officepcstore.payload.request.ProductReq;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.service.ProductService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;
    private final JwtUtils jwtUtils;
//    @GetMapping(path = "/products/{id}")
//    public ResponseEntity<?> findById (@PathVariable("id") String id, HttpServletRequest request){
//        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
//        return productService.findById(id, user.getId());
//    }
    @GetMapping(path = "/products/enable/{id}")
    public ResponseEntity<?> findById (@PathVariable("id") String id){
        return productService.findById(id);
    }

    @GetMapping(path = "/products/category/get/all/{id}")
    public ResponseEntity<?> findByCategoryId (@PathVariable("id") String id,
                                                         @ParameterObject Pageable pageable){
        return productService.findByCategoryId(id, pageable);
    }
    @GetMapping(path = "/products/brand/get/all/{id}")
    public ResponseEntity<?> findByBrandId (@PathVariable("id") String id,
                                               @ParameterObject Pageable pageable){
        return productService.findByBrandId(id, pageable);
    }

    @GetMapping(path = "/products/find/search")
    public ResponseEntity<?> search (@RequestParam("q") String query,
                                     @PageableDefault(sort = "score") @ParameterObject Pageable pageable){
        if (query.isEmpty() || query.matches(".*[%<>&;'\0-].*"))
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid keyword");
        return productService.search(query, pageable);
    }

//    @GetMapping(path = "/products/find/filter/search/config")
//    public ResponseEntity<?> filterProductConfig (@RequestParam Map<String, String> config,
//                                     @PageableDefault(size=20,sort = "createDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable){
//
//        return productService.filterProductByConfig(config, pageable);
//    }
//
//


    @GetMapping(path = "/products/get/enable/list/all")
    public ResponseEntity<?> findAllProductByUser (@PageableDefault(size = 20,sort = "createdDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable){
        return productService.findAllProductByUser(pageable);
    }

    @GetMapping(path = "/products/get/filer/price/list/all")
    public ResponseEntity<?> findAllProductFilerPriceByUser (@RequestParam(value = "min" ) BigDecimal min,
                                                             @RequestParam(value = "max" ) BigDecimal max,
                                                                 @PageableDefault(size = 20,sort = "createdDate", direction = Sort.Direction.DESC)
                                                                 @ParameterObject Pageable pageable){
        return productService.filterProductPriceByUser(min,max,pageable);
    }


    @GetMapping(path = "/admin/manage/products/get/all/list")
    public ResponseEntity<?> findAllProductByAdmin (@RequestParam(value = "state", defaultValue = "") String state,
                                                    @PageableDefault(size = 20,sort = "createdDate", direction = Sort.Direction.DESC)
                                      @ParameterObject Pageable pageable){
        return productService.findAllProductByAdmin(state,pageable);
    }

    @GetMapping(path = "/admin/manage/products/get/detail/{id}")
    public ResponseEntity<?> findByIdInAdmin (@PathVariable("id") String id){
        return productService.findByIdInAdmin(id);
    }

    @PostMapping("/admin/manage/product/create")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductReq req) {
        return productService.createProduct(req);
    }
//
    @PutMapping("/admin/manage/products/detail/update/{id}")
    public ResponseEntity<?> updateDetailProduct(@PathVariable("id") String id,
                                           @Valid @RequestBody ProductReq req) {
        return productService.updateDetailsProduct(id, req);
    }
//
//    @DeleteMapping("/manage/products/{id}")
//    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) {
//        return productService.deactivatedProduct(id);
//    }
//
//    @DeleteMapping("/manage/products/destroy/{id}")
//    public ResponseEntity<?> destroyProduct(@PathVariable("id") String id) {
//        return productService.destroyProduct(id);
//    }


    @PutMapping("/admin/manage/products/update/config/{productId}")
    public ResponseEntity<?> updateProductConfig(@PathVariable("productId") String productId,@RequestBody List<Map<String, String>> mapList) {
        return productService.updateProductConfig(productId,mapList);
    }

    @PostMapping(value = "/admin/manage/products/add/new/images/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addImages(@PathVariable("productId") String id ,
                                       @ModelAttribute AddImageReq req) {
        return productService.addImagesToProduct(id, req.getFiles());
    }

    @DeleteMapping("/admin/manage/products/delete/detail/images/{productId}")
    public ResponseEntity<?> deleteImage(@PathVariable("productId") String id,
                                         @RequestBody AddImageReq req) {
        return productService.deleteAllImageProduct(id, req.getImage_Id());
    }

//    @PutMapping("/manage/products/price")
//    public ResponseEntity<?> updatePriceAndDiscount(@Valid @RequestBody ProductPriceAndDiscount req) {
//        if (req.getId().contains(",")) return productService.updateMultiplePriceAndDiscount(req);
//        else return productService.updatePriceAndDiscount(req);
//    }
}
