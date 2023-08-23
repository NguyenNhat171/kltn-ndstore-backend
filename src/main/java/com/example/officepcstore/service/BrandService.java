package com.example.officepcstore.service;


import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.repository.BrandRepository;
import com.mongodb.MongoWriteException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final CloudinaryConfig cloudinary;


    public ResponseEntity<ResponseObjectData> findAll() {
        List<Brand> list = brandRepository.findAll();
        if (list.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all brand success", list));
        throw new NotFoundException("Can not found any brand");
    }


    public ResponseEntity<?> findAll(String state) {
        List<Brand> list;
        if (state == null || state.isBlank()) list=  brandRepository.findAll();
        else list=  brandRepository.findAllByState(state.toLowerCase(Locale.ROOT));
        if (list.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all brand success", list));
        throw new NotFoundException("Can not found any brand");
    }


    public ResponseEntity<?> findBrandById(String id) {
        Optional<Brand> brand = brandRepository.findBrandByIdAndState(id, Constant.ENABLE);
        if (brand.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get brand success", brand));
        throw new NotFoundException("Can not found brand with id: " + id);
    }


    @Transactional
    public ResponseEntity<?> addBrand(String name, MultipartFile file) {
        String imgUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                imgUrl = cloudinary.uploadImage(file, null);
            } catch (IOException e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload image");
            }
        }
        Brand brand = new Brand(name, imgUrl , Constant.ENABLE);
        try {
            brandRepository.save(brand);
        } catch (MongoWriteException e) {
            throw new AppException(HttpStatus.CONFLICT.value(), "Brand name already exists");
        } catch (Exception e) {
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObjectData(true, "Create brand success", brand));
    }


    @Transactional
    public ResponseEntity<?> updateBrand(String id, String name, String state, MultipartFile file) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            brand.get().setName(name);
            brand.get().setState(state);
            if (file != null && !file.isEmpty()) {
                try {
                    String imgUrl = cloudinary.uploadImage(file, brand.get().getImage());
                    brand.get().setImage(imgUrl);
                } catch (IOException e) {
                    throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload image");
                }
            }
            try {
                brandRepository.save(brand.get());
            } catch (MongoWriteException e) {
                throw new AppException(HttpStatus.CONFLICT.value(), "Brand name already exists");
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Update brand success", brand));
        }
        throw new NotFoundException(" Not found brand with id: " + id);
    }


    @Transactional
    public ResponseEntity<?> blockBrand(String id) {
        Optional<Brand> brand = brandRepository.findBrandByIdAndState(id, Constant.ENABLE);
        if (brand.isPresent()) {
            if (!brand.get().getProducts().isEmpty()) throw new AppException(HttpStatus.CONFLICT.value(),
                    "Product exist");
            brand.get().setState(Constant.DISABLE);
            brandRepository.save(brand.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "block brand success with id: "+id,""));
        } else throw new NotFoundException("Can not found brand with id: " + id);
    }
}
