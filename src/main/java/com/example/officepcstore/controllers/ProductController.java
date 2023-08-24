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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @GetMapping(path = "/{id}")
    public ResponseEntity<?> findById (@PathVariable("id") String id){
        return productService.findbyId(id);
    }

    @GetMapping(path = "/products/category/{id}")
    public ResponseEntity<?> findByCategoryIdAndBrandId (@PathVariable("id") String id,
                                                         @ParameterObject Pageable pageable){
        return productService.findByCategoryIdOrBrandId(id, pageable);
    }

    @GetMapping(path = "/products/search")
    public ResponseEntity<?> search (@RequestParam("q") String query,
                                     @PageableDefault(sort = "score") @ParameterObject Pageable pageable){
        if (query.isEmpty() || query.matches(".*[%<>&;'\0-].*"))
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid keyword");
        return productService.search(query, pageable);
    }

    @GetMapping(path = "/products")
    public ResponseEntity<?> findAllByState (@ParameterObject Pageable pageable){
        return productService.findAll(Constant.ENABLE, pageable);
    }

    @GetMapping(path = "/manage/products")
    public ResponseEntity<?> findAll (@RequestParam(value = "state", defaultValue = "") String state,
                                      @ParameterObject Pageable pageable){
        return productService.findAll(state,pageable);
    }

    @PostMapping("/manage/products")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductReq req) {
        return productService.createProduct(req);
    }
//
//    @PutMapping("/manage/products/{id}")
//    public ResponseEntity<?> updateProduct(@PathVariable("id") String id,
//                                           @Valid @RequestBody ProductReq req) {
//        return productService.updateProduct(id, req);
//    }
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




    @PostMapping(value = "/manage/products/images/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addImages(@PathVariable("productId") String id ,
                                       @ModelAttribute AddImageReq req) {
        return productService.addImagesToProduct(id, req.getFiles());
    }

    @DeleteMapping("/manage/products/images/{productId}")
    public ResponseEntity<?> deleteImage(@PathVariable("productId") String id,
                                         @RequestBody AddImageReq req) {
        return productService.deleteImageFromProduct(id, req.getImage_Id());
    }

//    @PutMapping("/manage/products/price")
//    public ResponseEntity<?> updatePriceAndDiscount(@Valid @RequestBody ProductPriceAndDiscount req) {
//        if (req.getId().contains(",")) return productService.updateMultiplePriceAndDiscount(req);
//        else return productService.updatePriceAndDiscount(req);
//    }
}
