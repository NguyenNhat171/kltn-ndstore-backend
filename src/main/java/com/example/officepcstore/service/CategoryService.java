package com.example.officepcstore.service;

import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.CategoryMap;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.models.enity.Category;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.CategoryReq;
import com.example.officepcstore.payload.response.BrandResponse;
import com.example.officepcstore.payload.response.CategoryResponse;
import com.example.officepcstore.repository.CategoryRepository;
import com.example.officepcstore.repository.ProductRepository;
import com.mongodb.MongoWriteException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CloudinaryConfig cloudinary;
    private final ProductRepository productRepository;
private final CategoryMap categoryMap;




    public ResponseEntity<ResponseObjectData> findAllByAdmin(String state, Pageable pageable) {
        Page<Category> listCategory;
        if(state == null || state.isBlank())
            listCategory = categoryRepository.findAll(pageable);
        else
            listCategory = categoryRepository.findAllByState(state, pageable);
        List<CategoryResponse> brandResList = listCategory.stream().map(categoryMap::getCategoryResponse).collect(Collectors.toList());
        Map<String, Object> cateResp = new HashMap<>();
        cateResp.put("totalPage", listCategory.getTotalPages());
        cateResp.put("totalCategory", listCategory.getTotalElements());
        cateResp.put("listCategory",brandResList);
        if (brandResList.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all category success", cateResp));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not Found any category", ""));
    }

    public ResponseEntity<?> findAllByUser() {
        List<Category> list = categoryRepository.findAllByState(Constant.ENABLE);
        if (list.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get CategorySuccess", list));
        throw new NotFoundException("Not found any category");
    }


    public ResponseEntity<?> findCategoryById(String id) {
        Optional<Category> category = categoryRepository.findCategoryByIdAndState(id, Constant.ENABLE);
        if (category.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get category success", category));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not found category"+id, ""));
    }

    public ResponseEntity<?> findCategoryByIdInAdmin(String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get category success", category));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not found category"+id,""));
    }


    @Transactional
    public ResponseEntity<?> addCategory(CategoryReq req) {
        String imgUrl = null;
        if (req.getFile() != null && !req.getFile().isEmpty()) {
            try {
                imgUrl = cloudinary.uploadImage(req.getFile(), null);
            } catch (IOException e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Upload image Error");
            }
        }
        Category category = new Category(req.getName(), imgUrl, Constant.ENABLE);
        categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObjectData(true, "create category success", category));

    }


    @Transactional
    public ResponseEntity<?> updateCategory(String id, CategoryReq req) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            category.get().setTitleCategory(req.getName());
            category.get().setState(req.getState());
                categoryRepository.save(category.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Update complete", category));
        }
        throw new NotFoundException("Not found category id: " + id);
    }


    @Transactional
    public ResponseEntity<?> updateCategoryImage(String id, MultipartFile file) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            if (file != null && !file.isEmpty()) {
                try {
                    String imgUrl = cloudinary.uploadImage(file, category.get().getImageCategory());
                    category.get().setImageCategory(imgUrl);
                    categoryRepository.save(category.get());
                } catch (IOException e) {
                    throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload image");
                }
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Update image complete", category));
            }
        }
        throw new NotFoundException("Can not found category with id: " + id);
    }


    @Transactional
    public ResponseEntity<?> changeStateDisableCategory(String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            if (!category.get().getDependentProducts().isEmpty()) throw new AppException(HttpStatus.CONFLICT.value(),
                    "Exist Product.");
            category.get().setState(Constant.DISABLE);
            categoryRepository.save(category.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Deactivated category success", id));
        } else throw new NotFoundException("Not found category with id: " + id);
    }
    @Transactional
    public ResponseEntity<?> changeStateDisableCategoryNew (String id){
        Optional<Category> category = categoryRepository.findById(id);
        List<Product> products = productRepository.findAllByCategory_IdAndState(new ObjectId(id),Constant.ENABLE);
        if (category.isPresent()) {
            if (products.size()>0)
                throw new AppException(HttpStatus.CONFLICT.value(),
                        "Product exist");
            category.get().setState(Constant.DISABLE);
            categoryRepository.save(category.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Block brand success with id: " + id, ""));
        } else  return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found Brand " + id, ""));
    }
    @Transactional
    public ResponseEntity<?> changeStateEnableCategory(String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent() &&category.get().getState().equals(Constant.DISABLE)) {

            category.get().setState(Constant.ENABLE);
            categoryRepository.save(category.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Deactivated category success", id));
        } else throw new NotFoundException("Not found category with id: " + id);
    }
}
