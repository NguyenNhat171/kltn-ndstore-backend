package com.example.officepcstore.service;

import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.ProductMap;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.models.enity.Category;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.models.enity.product.ProductImage;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.ProductReq;
import com.example.officepcstore.payload.response.AllProductResponse;
import com.example.officepcstore.payload.response.ProductResponse;
import com.example.officepcstore.repository.BrandRepository;
import com.example.officepcstore.repository.CategoryRepository;
import com.example.officepcstore.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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


    public ResponseEntity<?> findAllProductByUser( Pageable pageable) {
        Page<Product> products = productRepository.findAllByState(Constant.ENABLE,pageable);
        List<AllProductResponse> listProduct  = products.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        ResponseEntity<?> listProductRes = getPageProductRes(products, listProduct);
        if (listProductRes != null)
            return listProductRes;
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
    }

    public ResponseEntity<?> findAllListProductByUser() {
        List<Product> products = productRepository.findAllByState(Constant.ENABLE);
        List<AllProductResponse> listProduct  = products.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        Map<String, Object> mapListProduct = new HashMap<>();
        mapListProduct .put("list", listProduct);
        mapListProduct .put("totalQuantity", products.size());
        if (products.size()>0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get product success", mapListProduct));
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

    public ResponseEntity<?> filterProductByConfigAndCategoryId( String categoryId,String brandId,Map<String, String> optionProduct,Pageable pageable)
    {
        Page<Product> pageFilteredProducts;
        List<Map<String, String>> queryParamsList = new ArrayList<>();
        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("page") || key.equals("size")|| key.equals("categoryId")||key.equals("brandId"))
            {
                query.remove("page");
                query.remove("size");
                query.remove("categoryId");
                query.remove("brandId");
            }
            else
                query.put(key, value);
            queryParamsList.add(query);
        });
        if(categoryId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
        }
            else{
            if (optionProduct.isEmpty()) {
                pageFilteredProducts = productRepository.findAllByCategory_IdAndState(new ObjectId(categoryId), Constant.ENABLE,pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else if(optionProduct.isEmpty() && !brandId.isBlank() )
            {
                pageFilteredProducts = productRepository.findAllByCategory_IdAndBrand_IdAndState(new ObjectId(categoryId),new ObjectId(brandId), Constant.ENABLE,pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }

            else if(!optionProduct.isEmpty()&& brandId.isBlank()){
                pageFilteredProducts = productRepository.findAllByProductConfigurationAndCategory_Id(queryParamsList, new ObjectId(categoryId),pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else {
                pageFilteredProducts = productRepository.findAllByProductConfigurationAndCategory_IdAndBrand_Id(queryParamsList,new ObjectId(categoryId),new ObjectId(brandId) ,pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp =getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            }
    }


    public ResponseEntity<?> filterPriceAndProductByConfigAndCategoryId(String categoryId,String brandId, Map<String, String> optionProduct, BigDecimal priceMin, BigDecimal priceMax, Pageable pageable)
    {
        Page<Product> pageFilteredProducts;
        List<Map<String, String>> queryParamsList = new ArrayList<>();

        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("page") || key.equals("size")|| key.equals("categoryId")||key.equals("brandId") ||key.equals("priceMin") ||key.equals("priceMax"))
            {
                query.remove("page");
                query.remove("size");
                query.remove("categoryId");
                query.remove("priceMin");
                query.remove("priceMax");
            }
            else
                query.put(key, value);
            queryParamsList.add(query);
        });
        if(categoryId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found category", ""));
        }
        else {
            if (optionProduct.isEmpty()) {
                pageFilteredProducts = productRepository.findAllByCategory_IdAndState(new ObjectId(categoryId), Constant.ENABLE,pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else if(optionProduct.isEmpty() && !brandId.isBlank() )
            {
                pageFilteredProducts = productRepository.findAllByCategory_IdAndBrand_IdAndState(new ObjectId(categoryId),new ObjectId(brandId), Constant.ENABLE,pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }

            else if(!optionProduct.isEmpty()&& brandId.isBlank()){
                Long priceMinLong = priceMin.longValue();
                Long priceMaxLong = priceMax.longValue();
                pageFilteredProducts = productRepository.findAllByProductConfigurationAndCategory_IdAndReducedPriceBetween(queryParamsList, new ObjectId(categoryId), priceMinLong, priceMaxLong,pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else {
                Long priceMinLong = priceMin.longValue();
                Long priceMaxLong = priceMax.longValue();
                pageFilteredProducts = productRepository.findAllByProductConfigurationAndCategory_IdAndBrand_IdAndReducedPriceBetween(queryParamsList,new ObjectId(categoryId),new ObjectId(brandId) , priceMinLong, priceMaxLong,pageable);
                List<AllProductResponse> listProduct = pageFilteredProducts.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp =getPageProductRes(pageFilteredProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }

        }
    }




    public ResponseEntity<?> listFilterProductByConfigAndCategoryId( String categoryId,String brandId,Map<String, String> optionProduct)
    {
        List<Product> listFilterProducts ;
        List<Map<String, String>> optionParamsList = new ArrayList<>();
        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("page") || key.equals("size")|| key.equals("categoryId")|| key.equals("brandId"))
            {
                query.remove("page");
                query.remove("size");
                query.remove("categoryId");
                query.remove("brandId");
            }
            else
                query.put(key, value);
            optionParamsList.add(query);
        });
        if(categoryId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
        }
        else{
            if (optionProduct.isEmpty()) {
                listFilterProducts = productRepository.findAllByCategory_IdAndState(new ObjectId(categoryId), Constant.ENABLE);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else if(optionProduct.isEmpty() && !brandId.isBlank() )
            {
                listFilterProducts = productRepository.findAllByCategory_IdAndBrand_IdAndState(new ObjectId(categoryId),new ObjectId(brandId), Constant.ENABLE);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }

            else if(!optionProduct.isEmpty()&& brandId.isBlank()){
                listFilterProducts = productRepository.findAllByProductConfigurationAndCategory_Id(optionParamsList, new ObjectId(categoryId));
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else {
                listFilterProducts = productRepository.findAllByProductConfigurationAndCategory_IdAndBrand_Id(optionParamsList,new ObjectId(categoryId),new ObjectId(brandId));
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }

            //
        }
    }



//    public ResponseEntity<?> searchListFilterProductByKeywordAndConfigAndBrand( String categoryId,String brandId,Map<String, String> optionProduct)
//    {
//        List<Product> listFilterProducts ;
//        List<Map<String, String>> optionParamsList = new ArrayList<>();
//        optionProduct.forEach((key, value) -> {
//            Map<String, String> query = new HashMap<>();
//            if(key.equals("page") || key.equals("size")|| key.equals("categoryId")|| key.equals("brandId"))
//            {
//                query.remove("page");
//                query.remove("size");
//                query.remove("categoryId");
//                query.remove("brandId");
//            }
//            else
//                query.put(key, value);
//            optionParamsList.add(query);
//        });
//        if(categoryId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObjectData(false, "Not found any product", ""));
//        }
//        else{
//            if (optionProduct.isEmpty()) {
//                listFilterProducts = productRepository.findAllByCategory_IdAndState(new ObjectId(categoryId), Constant.ENABLE);
//                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
//                if (resp != null)
//                    return resp;
//                else
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                            new ResponseObjectData(false, "Not found any product", ""));
//            }
//            else if(optionProduct.isEmpty() && !brandId.isBlank() )
//            {
//                listFilterProducts = productRepository.findAllByCategory_IdAndBrand_IdAndState(new ObjectId(categoryId),new ObjectId(brandId), Constant.ENABLE);
//                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
//                if (resp != null)
//                    return resp;
//                else
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                            new ResponseObjectData(false, "Not found any product", ""));
//            }
//
//            else if(!optionProduct.isEmpty()&& brandId.isBlank()){
//                listFilterProducts = productRepository.findAllByProductConfigurationAndCategory_Id(optionParamsList, new ObjectId(categoryId));
//                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
//                if (resp != null)
//                    return resp;
//                else
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                            new ResponseObjectData(false, "Not found any product", ""));
//            }
//            else {
//                listFilterProducts = productRepository.findAllByProductConfigurationAndCategory_IdAndBrand_Id(optionParamsList,new ObjectId(categoryId),new ObjectId(brandId));
//                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
//                if (resp != null)
//                    return resp;
//                else
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                            new ResponseObjectData(false, "Not found any product", ""));
//            }
//
//            //
//        }
//    }

    public ResponseEntity<?> searchKeyListFilterBrandAndProductByConfig(String keyword,String brandId, Map<String, String> optionProduct/*, BigDecimal priceMin, BigDecimal priceMax8*/)
    {
       List<Product> listFilterProducts;
        List<Map<String, String>> listOptionProduct= new ArrayList<>();

        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("keyword") || key.equals("page") || key.equals("size")||key.equals("priceMin") ||key.equals("priceMax")||key.equals("brandId"))
            {
                query.remove("page");
                query.remove("size");
//                query.remove("priceMin");
//                query.remove("priceMax");
                query.remove("brandId");
                query.remove("keyword");
            }
            else
                query.put(key, value);
            listOptionProduct.add(query);
        });
        if(keyword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found product", ""));
        }

        else {

            if (optionProduct.isEmpty() && !brandId.isBlank()) {
                listFilterProducts = productRepository.findAllByKeywordAndBrand_Id(keyword,new ObjectId(brandId));
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else if(!optionProduct.isEmpty() && brandId.isBlank() )
            {
                listFilterProducts = productRepository.findAllByProductConfigurationAndKeyword(listOptionProduct,keyword);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }

//            else if(!optionProduct.isEmpty()&& !brandId.isBlank()){
//                Long priceMinLong = priceMin.longValue();
//                Long priceMaxLong = priceMax.longValue();
//                listFilterProducts = productRepository.findAllByProductConfigurationAndBrand_IdAndReducedPriceBetweenAndKeyword(listOptionProduct, new ObjectId(brandId), priceMinLong, priceMaxLong,keyword);
//                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
//                if (resp != null)
//                    return resp;
//                else
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                            new ResponseObjectData(false, "Not found any product", ""));
//            }
            else {
                listFilterProducts = productRepository.findAllByKeyword(keyword);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }


        }
    }


    public ResponseEntity<?> searchKeyListFilterBrandAndPriceAndProductByConfig(String keyword,String brandId, Map<String, String> optionProduct, BigDecimal priceMin, BigDecimal priceMax)
    {
        Long priceMinLong = priceMin.longValue();
        Long priceMaxLong = priceMax.longValue();
        List<Product> listFilterProducts;
        List<Map<String, String>> listOptionProduct= new ArrayList<>();

        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("keyword") || key.equals("page") || key.equals("size")||key.equals("priceMin") ||key.equals("priceMax")||key.equals("brandId"))
            {
                query.remove("page");
                query.remove("size");
               query.remove("priceMin");
                query.remove("priceMax");
                query.remove("brandId");
                query.remove("keyword");
            }
            else
                query.put(key, value);
            listOptionProduct.add(query);
        });
        if(keyword.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found product", ""));
        }

        else {

//            if (optionProduct.isEmpty() && !brandId.isBlank()&&priceMaxLong.equals(-10) && priceMinLong.equals(-10)) {
//                listFilterProducts = productRepository.findAllByKeywordAndBrand_Id(keyword,new ObjectId(brandId));
//                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
//                if (resp != null)
//                    return resp;
//                else
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                            new ResponseObjectData(false, "Not found any product", ""));
//            }
//            else if(!optionProduct.isEmpty() && brandId.isBlank() )
//            {
//                listFilterProducts = productRepository.findAllByProductConfigurationAndKeyword(listOptionProduct,keyword);
//                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
//                if (resp != null)
//                    return resp;
//                else
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                            new ResponseObjectData(false, "Not found any product", ""));
//            }

//            else if(!priceMinLong.equals(-10)&&!priceMaxLong.equals(-10)){
                if(!brandId.isBlank() && optionProduct.isEmpty()){
                    listFilterProducts = productRepository.findAllByBrand_IdAndReducedPriceBetweenAndKeyword(new ObjectId(brandId),priceMinLong,priceMaxLong,keyword);
                }
                else if(brandId.isBlank() && !optionProduct.isEmpty())
                {
                    listFilterProducts = productRepository.findAllByProductConfigurationAndReducedPriceBetweenAndKeyword(listOptionProduct,priceMinLong,priceMaxLong,keyword);
                }
                else if(!brandId.isBlank() && !optionProduct.isEmpty()){
                    listFilterProducts = productRepository.findAllByProductConfigurationAndBrand_IdAndReducedPriceBetweenAndKeyword(listOptionProduct, new ObjectId(brandId), priceMinLong, priceMaxLong, keyword);
                }
                else if(brandId.isBlank() && optionProduct.isEmpty())
                {
                    listFilterProducts = productRepository.findAllByPriceBetweenAndKeyword(priceMinLong,priceMaxLong,keyword);
                }
            else {
                listFilterProducts = productRepository.findAllByKeyword(keyword);
            }
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));



        }
    }


    public ResponseEntity<?> searchKeyPageFilterBrandAndPriceAndProductByConfig(String keyword,String brandId, Map<String, String> optionProduct, BigDecimal priceMin, BigDecimal priceMax,Pageable pageable)
    {
        Long priceMinLong = priceMin.longValue();
        Long priceMaxLong = priceMax.longValue();
        Page<Product> listFilterProducts;
        List<Map<String, String>> listOptionProduct= new ArrayList<>();

        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("keyword") || key.equals("page") || key.equals("size")||key.equals("priceMin") ||key.equals("priceMax")||key.equals("brandId"))
            {
                query.remove("page");
                query.remove("size");
                query.remove("priceMin");
                query.remove("priceMax");
                query.remove("brandId");
                query.remove("keyword");
            }
            else
                query.put(key, value);
            listOptionProduct.add(query);
        });
        if(keyword.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found product", ""));
        }

        else {
            if(!brandId.isBlank() && optionProduct.isEmpty()){
                listFilterProducts = productRepository.findAllByBrand_IdAndReducedPriceBetweenAndKeyword(new ObjectId(brandId),priceMinLong,priceMaxLong,keyword,pageable);
            }
            else if(brandId.isBlank() && !optionProduct.isEmpty())
            {
                listFilterProducts = productRepository.findAllByProductConfigurationAndReducedPriceBetweenAndKeyword(listOptionProduct,priceMinLong,priceMaxLong,keyword,pageable);
            }
            else if(!brandId.isBlank() && !optionProduct.isEmpty()){
                listFilterProducts = productRepository.findAllByProductConfigurationAndBrand_IdAndReducedPriceBetweenAndKeyword(listOptionProduct, new ObjectId(brandId), priceMinLong, priceMaxLong, keyword,pageable);
            }

            else if(brandId.isBlank() && optionProduct.isEmpty())
            {
                listFilterProducts = productRepository.findAllByPriceBetweenAndKeyword(priceMinLong,priceMaxLong,keyword,pageable);
            }
            else {
                listFilterProducts = productRepository.findAllByKeyword(keyword,pageable);
            }
            List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
            ResponseEntity<?> resp = getPageProductRes(listFilterProducts, listProduct);
            if (resp != null)
                return resp;
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObjectData(false, "Not found any product", ""));



        }
    }

    public ResponseEntity<?> listFilterPriceAndProductByConfigAndCategoryId(String categoryId,String brandId, Map<String, String> optionProduct, BigDecimal priceMin, BigDecimal priceMax)
    {
        List<Product> listFilterProducts;
        List<Map<String, String>> listOptionProduct= new ArrayList<>();

        optionProduct.forEach((key, value) -> {
            Map<String, String> query = new HashMap<>();
            if(key.equals("page") || key.equals("size")|| key.equals("categoryId")||key.equals("priceMin") ||key.equals("priceMax")||key.equals("brandId"))
            {
                query.remove("page");
                query.remove("size");
                query.remove("categoryId");;
                query.remove("priceMin");
                query.remove("priceMax");
                query.remove("brandId");
            }
            else
                query.put(key, value);
            listOptionProduct.add(query);
        });
        if(categoryId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found category", ""));
        }

        else {

            if (optionProduct.isEmpty()) {
                listFilterProducts = productRepository.findAllByCategory_IdAndState(new ObjectId(categoryId), Constant.ENABLE);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else if(optionProduct.isEmpty() && !brandId.isBlank() )
            {
                listFilterProducts = productRepository.findAllByCategory_IdAndBrand_IdAndState(new ObjectId(categoryId),new ObjectId(brandId), Constant.ENABLE);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }

            else if(!optionProduct.isEmpty()&& brandId.isBlank()){
                Long priceMinLong = priceMin.longValue();
                Long priceMaxLong = priceMax.longValue();
                listFilterProducts = productRepository.findAllByProductConfigurationAndCategory_IdAndReducedPriceBetween(listOptionProduct, new ObjectId(categoryId), priceMinLong, priceMaxLong);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }
            else {
                Long priceMinLong = priceMin.longValue();
                Long priceMaxLong = priceMax.longValue();
                listFilterProducts = productRepository.findAllByProductConfigurationAndCategory_IdAndBrand_IdAndReducedPriceBetween(listOptionProduct,new ObjectId(categoryId),new ObjectId(brandId) , priceMinLong, priceMaxLong);
                List<AllProductResponse> listProduct = listFilterProducts.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
                ResponseEntity<?> resp = toGetListProductResponse(listFilterProducts, listProduct);
                if (resp != null)
                    return resp;
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObjectData(false, "Not found any product", ""));
            }


        }
    }


    private ResponseEntity<?> toGetListProductResponse(List<Product> products, List<AllProductResponse> resAll)
    {
        Map<String, Object> respList = new HashMap<>();
        respList.put("list", resAll);
        respList.put("totalQuantity", products.size());
        if (!resAll.isEmpty() )
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "All product Success ",respList));
        return null;
    }



    public ResponseEntity<?> findByCategoryId(String id, Pageable pageable) {
        Page<Product> products;

            Optional<Category> category = categoryRepository.findCategoryByIdAndDisplay(id, Constant.ENABLE);
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
        Optional<Brand> brand = brandRepository.findBrandByIdAndDisplay(id, Constant.ENABLE);
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
    public ResponseEntity<?> searchProductByKeyword(String keywordProduct, Pageable pageable) {
        Page<Product> products;
        try {
            products = productRepository.findAllByKeyword(keywordProduct, pageable);
        } catch (Exception e) {
            throw new NotFoundException("Can not found any product with: "+keywordProduct);
        }
        List<AllProductResponse> resList = products.getContent().stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        ResponseEntity<?> resp = getPageProductRes(products, resList);
        if (resp != null) return resp;
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
    }

    public ResponseEntity<?> searchProductByKeywordReturnList(String keywordProduct) {
       List<Product> products;
        try {
            products = productRepository.findAllByKeyword(keywordProduct);
        } catch (Exception e) {
            throw new NotFoundException("Can not found any product with: " + keywordProduct);
        }
        List<AllProductResponse> resList = products.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        ResponseEntity<?> resp = toGetListProductResponse(products, resList);
        if (resp != null) return resp;
        else
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not found any product", ""));
    }

    public ResponseEntity<?> createProduct(ProductReq req) {
        if (req != null) {
            Product product = productMap.putProductModel(req);
                productRepository.save(product);
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
            product.get().setProductDetail(productReq.getDescription());
            product.get().setPrice(productReq.getPrice());
            product.get().setStock(productReq.getStock());
            product.get().setDiscount(productReq.getDiscount());
            if (!productReq.getCategory().equals(product.get().getCategory().getId())) {
                Optional<Category> category = categoryRepository.findById(productReq.getCategory());
                if (category.isPresent())
                    product.get().setCategory(category.get());
                else throw new NotFoundException("Not found category with id: "+productReq.getCategory());
            }
            if (!productReq.getBrand().equals(product.get().getBrand().getId())) {
                Optional<Brand> brand = brandRepository.findById(productReq.getBrand());
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
