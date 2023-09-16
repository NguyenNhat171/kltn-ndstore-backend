package com.example.officepcstore.service;

import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Category;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.CategoryReq;
import com.example.officepcstore.repository.CategoryRepository;
import com.mongodb.MongoWriteException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CloudinaryConfig cloudinary;


    public ResponseEntity<?> findAll() {
   List<Category> list = categoryRepository.findAll();
        if (list.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "List Category Success", list));
        throw new NotFoundException("Not found category");
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
        throw new NotFoundException("Not found category id: " + id);
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


//    @Transactional
//    public ResponseEntity<?> deactivatedCategory(String id) {
//        Optional<Category> category = categoryRepository.findById(id);
//        if (category.isPresent()) {
//            if (!category.get().getProducts().isEmpty()) throw new AppException(HttpStatus.CONFLICT.value(),
//                    "There's a product belongs to that category.");
//            category.get().setState(Constant.DISABLE);
//            category.get().getSubCategories().forEach(c -> c.setState(Constant.DISABLE));
//            categoryRepository.saveAll(category.get().getSubCategories());
//            categoryRepository.save(category.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Deactivated category success", id));
//        } else throw new NotFoundException("Can not found category with id: " + id);
//    }
}
