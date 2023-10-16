package com.example.officepcstore.service;

import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.ProductMap;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.models.enity.Category;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.models.enity.product.ProductImage;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.ProductReq;
import com.example.officepcstore.payload.response.AllProductResponse;
import com.example.officepcstore.payload.response.ProductResponse;
import com.example.officepcstore.repository.BrandRepository;
import com.example.officepcstore.repository.CategoryRepository;
import com.example.officepcstore.repository.ProductRepository;
import com.example.officepcstore.repository.UserRepository;
import com.example.officepcstore.utils.RecommendProductUtils;
import com.example.officepcstore.utils.StringUtils;
import com.mongodb.MongoWriteException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMap productMap;
    private final CloudinaryConfig cloudinary;
//    private final RecommendProductUtils recommendCheckUtils;
//    private final TaskScheduler taskScheduler;
//    private final UserRepository userRepository;
//    public ResponseEntity<?> findAll(String state, Pageable pageable) {
//        Page<Product> products;
//        if (state.equalsIgnoreCase(Constant.ENABLE) || state.equalsIgnoreCase(Constant.DISABLE))
//            products = productRepository.findAllByState(state.toLowerCase(), pageable);
//        else products = productRepository.findAll(pageable);
//        List<AllProductResponse> resList = products.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//        ResponseEntity<?> resp = getPageProductRes(products, resList);
//        if (resp != null) return resp;
//        throw new NotFoundException("Can not found any product");
//    }

    public ResponseEntity<?> findAllProductByUser( Pageable pageable) {
        Page<Product> products;
            products = productRepository.findAllByState(Constant.ENABLE,pageable);
        List<AllProductResponse> listProduct  = products.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        ResponseEntity<?> listProductRes = getPageProductRes(products, listProduct);
        if (listProductRes != null)
            return listProductRes;
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
    }

    public ResponseEntity<?> findAllProductByAdmin(String state ,Pageable pageable) {
        Page<Product> products;
        if(state == null || state.isBlank())
           products = productRepository.findAll(pageable);
        else
        products = productRepository.findAllByState(state,pageable);
        List<AllProductResponse> listProduct  = products.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        ResponseEntity<?> listProductRes = getPageProductRes(products, listProduct);
        if (listProductRes != null)
            return listProductRes;
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found product", ""));
    }

    public ResponseEntity<?> filterProductPriceByUser(BigDecimal priceMin, BigDecimal priceMax, Pageable pageable ) {
        Long priceMinLong = priceMin.longValue();
        Long priceMaxLong = priceMax.longValue();
        Page<Product> products = productRepository.findAllByPriceBetweenAndState(priceMinLong,priceMaxLong,Constant.ENABLE,pageable);
   List<AllProductResponse> listProduct  = products.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
      ResponseEntity<?> listProductRes = getPageProductRes(products, listProduct);
      if (listProductRes != null)
       return listProductRes;
      else
          return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                  new ResponseObjectData(false, "Not found product", ""));
    }


    private ResponseEntity<?> getPageProductRes(Page<Product> products, List<AllProductResponse> resAll) //addPageableToRes
    {
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resAll);
        resp.put("totalQuantity", products.getTotalElements());
        resp.put("totalPage", products.getTotalPages());
        if (!resAll.isEmpty() )
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "All product Success ", resp));
        return null;
    }


//    public ResponseEntity<?> findByRecommentUserId(String id, String userId) {
//        Optional<Product> product = productRepository.findProductByIdAndState(id, Constant.ENABLE);
//        if (product.isPresent()) {
//            ProductResponse res = productMap.toGetProductRes(product.get());
//            recommendCheckUtils.setCatId(res.getCategoryId());
//            recommendCheckUtils.setBrandId(res.getBrandId());
//            recommendCheckUtils.setType(Constant.VIEW_TYPE);
//            recommendCheckUtils.setUserId(userId);
//            recommendCheckUtils.setUserRepository(userRepository);
//            taskScheduler.schedule(recommendCheckUtils, new Date(System.currentTimeMillis()));
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Get product success", res));
//        }
//        throw new NotFoundException("Can not found any product with id: "+id);
//    }
    public ResponseEntity<?> findById(String id) {
        Optional<Product> product = productRepository.findProductByIdAndState(id, Constant.ENABLE);
        if (product.isPresent()) {
            ProductResponse res = productMap.toGetProductRes(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get product Success ", res));
        }
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found product"+id, ""));
    }
    public ResponseEntity<?> findByIdInAdmin(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            ProductResponse res = productMap.toGetProductRes(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get product Success ", res));
        }
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found product"+id, ""));
    }

    public ResponseEntity<?> filterProductByConfig( Map<String, String> optionProduct,Pageable pageable)
    {
        List<Map<String, String>> queryParamsList = new ArrayList<>();
        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("page") || key.equals("size"))
            {
             query.remove("page");
             query.remove("size");
            }
            else
            query.put(key, value);
            queryParamsList.add(query);
        });
        if(optionProduct.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not choose option", ""));
        }
        else {
            Page<Product> filteredProducts = productRepository.findAllByProductConfiguration(queryParamsList, pageable);
            List<AllProductResponse> listProduct = filteredProducts.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
            ResponseEntity<?> resp = getPageProductRes(filteredProducts, listProduct);
            if (resp != null)
                return resp;
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "Not found any product", ""));
        }
    }

    public ResponseEntity<?> findByCategoryId(String id, Pageable pageable) {
        Page<Product> products;

            Optional<Category> category = categoryRepository.findCategoryByIdAndState(id, Constant.ENABLE);
            if (category.isPresent()) {
                products = productRepository.findAllByCategory_IdAndState(new ObjectId(id),Constant.ENABLE, pageable);
                List<AllProductResponse> resList = products.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = getPageProductRes(products, resList);
                if (resp != null)
                    return resp;
            }
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "Not found any product", ""));
    }


    public ResponseEntity<?> findByBrandId(String id, Pageable pageable) {
        Optional<Brand> brand = brandRepository.findBrandByIdAndState(id, Constant.ENABLE);
        if (brand.isPresent()) {
            Page<Product>   products = productRepository.findAllByBrand_IdAndState(new ObjectId(id),Constant.ENABLE, pageable);
            List<AllProductResponse> resList = products.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
            ResponseEntity<?> resp = getPageProductRes(products, resList);
            if (resp != null)
                return resp;
        }
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
    }
    public ResponseEntity<?> search(String key, Pageable pageable) {
        Page<Product> products;
        try {
            products = productRepository.findAllBy(TextCriteria
                            .forDefaultLanguage().matchingAny(key),
                    pageable);
        } catch (Exception e) {
            throw new NotFoundException("Can not found any product with: "+key);
        }
        List<AllProductResponse> resList = products.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        ResponseEntity<?> resp = getPageProductRes(products, resList);
        if (resp != null) return resp;
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
    }

    public ResponseEntity<?> createProduct(ProductReq req) {
        if (req != null) {
            Product product = productMap.putProductModel(req);
            try {
                productRepository.save(product);
            } catch (Exception e) {
                throw new AppException(HttpStatus.CONFLICT.value(), "Product  already exists");
            }
            ProductResponse res = productMap.toGetProductRes(product);
            product.setReducedPrice(res.getDiscountPrice());
            productRepository.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObjectData(true, "Create product successfully ", res)
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObjectData(false, "Request is null", "")
        );
    }
    public ResponseEntity<?> updateDetailsProduct(String id, ProductReq productReq) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent() && productReq != null) {
            product.get().setName(productReq.getName());
            product.get().setDescription(productReq.getDescription());
            product.get().setPrice(productReq.getPrice());
            product.get().setStock(productReq.getStock());
            product.get().setDiscount(productReq.getDiscount());
            if (!productReq.getCategory().equals(product.get().getCategory().getId())) {
                Optional<Category> category = categoryRepository.findCategoryByIdAndState(productReq.getCategory(), Constant.ENABLE);
                if (category.isPresent())
                    product.get().setCategory(category.get());
                else throw new NotFoundException("Not found category with id: "+productReq.getCategory());
            }
            if (!productReq.getBrand().equals(product.get().getBrand().getId())) {
                Optional<Brand> brand = brandRepository.findBrandByIdAndState(productReq.getBrand(), Constant.ENABLE);
                if (brand.isPresent())
                    product.get().setBrand(brand.get());
                else throw new NotFoundException("Not found brand with id: "+productReq.getBrand());
            }
            product.get().setState(productReq.getState());
            String discountCalculate = productReq.getPrice().multiply(BigDecimal.valueOf((double) (100- productReq.getDiscount())/100))
                    .stripTrailingZeros().toPlainString();
            BigDecimal discountPrice = new BigDecimal(discountCalculate);
            product.get().setReducedPrice(discountPrice);
                productRepository.save(product.get());
            ProductResponse res = productMap.toGetProductRes(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Update product successfully ", res)
            );
        }
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObjectData(false, "Not found product" +id, ""));
    }


    public ResponseEntity<?> updateProductConfig(String productId, List<Map<String, String>> mapList) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
           product.get().setProductConfiguration(mapList);
                productRepository.save(product.get());
            ProductResponse res = productMap.toGetProductRes(product.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObjectData(true, "Update product successfully ", res)
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObjectData(false, "Request is null", "")
        );
    }
    @Transactional
    public ResponseEntity<?> addImagesToProduct(String productId, List<MultipartFile> files) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            try {
                if (files == null || files.isEmpty() ) throw new AppException(HttpStatus.BAD_REQUEST.value(), "Images is empty");
                files.forEach(f -> {
                    try {
                        String url = cloudinary.uploadImage(f, null);
                        product.get().getProductImageList().add(new ProductImage(UUID.randomUUID().toString(), url));
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload images");
                    }
                    productRepository.save(product.get());
                });
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Add image to product complete", product.get().getProductImageList())
                );
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new NotFoundException("Error when save image: " + e.getMessage());
            }
        } throw new NotFoundException("Can not found product with id: " + productId);
    }

    @Transactional
    public ResponseEntity<?> deleteAllImageProduct(String productId, String imageId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent() && !product.get().getProductImageList().isEmpty()) {
            try {
                Optional<ProductImage> checkDelete = product.get().getProductImageList().stream().filter(i -> i.getId_image().equals(imageId)).findFirst();
                if (checkDelete.isPresent()) {
                    cloudinary.deleteImage(checkDelete.get().getUrl());
                    product.get().getProductImageList().remove(checkDelete.get());
                    productRepository.save(product.get());
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObjectData(true, "Delete image complete", imageId)
                    );
                } else throw new NotFoundException("Not found this id: " + imageId);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new NotFoundException("Not found this id: " + productId);
            }
        } throw new NotFoundException("Not found this id: " + productId);
    }


    @Transactional
    public ResponseEntity<?> changeStateProductByAdmin(String id) {
        Optional<Product>  product = productRepository.findById(id);
        if (product.isPresent()) {
            if(product.get().getState().equals(Constant.DISABLE)) {
                product.get().setState(Constant.ENABLE);
                productRepository.save(product.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Set enable product success", product));
            }
            else  {
                product.get().setState(Constant.DISABLE);
                productRepository.save(product.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Set disable product success", product));
            }

        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Can not found product with id" +id , ""));
    }
}
