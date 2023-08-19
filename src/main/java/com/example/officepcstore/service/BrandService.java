package com.example.officepcstore.service;


import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.repository.BrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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

//    @Override
//    public ResponseEntity<?> findAll(String state) {
//        List<Brand> list;
//        if (state == null || state.isBlank()) list=  brandRepository.findAll();
//        else list=  brandRepository.findAllByState(state.toLowerCase(Locale.ROOT));
//        if (list.size() > 0)
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject(true, "Get all brand success", list));
//        throw new NotFoundException("Can not found any brand");
//    }
//
//    @Override
//    public ResponseEntity<?> findBrandById(String id) {
//        Optional<Brand> brand = brandRepository.findBrandByIdAndState(id, Constants.ENABLE);
//        if (brand.isPresent())
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject(true, "Get brand success", brand));
//        throw new NotFoundException("Can not found brand with id: " + id);
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> addBrand(String name, MultipartFile file) {
//        String imgUrl = null;
//        if (file != null && !file.isEmpty()) {
//            try {
//                imgUrl = cloudinary.uploadImage(file, null);
//            } catch (IOException e) {
//                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload image");
//            }
//        }
//        Brand brand = new Brand(name, imgUrl , Constants.ENABLE);
//        try {
//            brandRepository.save(brand);
//        } catch (MongoWriteException e) {
//            throw new AppException(HttpStatus.CONFLICT.value(), "Brand name already exists");
//        } catch (Exception e) {
//            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body(
//                new ResponseObject(true, "Create brand success", brand));
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> updateBrand(String id, String name, String state, MultipartFile file) {
//        Optional<Brand> brand = brandRepository.findById(id);
//        if (brand.isPresent()) {
//            brand.get().setName(name);
//            brand.get().setState(state);
//            if (file != null && !file.isEmpty()) {
//                try {
//                    String imgUrl = cloudinary.uploadImage(file, brand.get().getImage());
//                    brand.get().setImage(imgUrl);
//                } catch (IOException e) {
//                    throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload image");
//                }
//            }
//            try {
//                brandRepository.save(brand.get());
//            } catch (MongoWriteException e) {
//                throw new AppException(HttpStatus.CONFLICT.value(), "Brand name already exists");
//            }
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject(true, "Update brand success", brand));
//        }
//        throw new NotFoundException("Can not found brand with id: " + id);
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> deactivatedBrand(String id) {
//        Optional<Brand> brand = brandRepository.findBrandByIdAndState(id, Constants.ENABLE);
//        if (brand.isPresent()) {
//            if (!brand.get().getProducts().isEmpty()) throw new AppException(HttpStatus.CONFLICT.value(),
//                    "There's a product belongs to that brand.");
//            brand.get().setState(Constants.DISABLE);
//            brandRepository.save(brand.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject(true, "delete brand success with id: "+id,""));
//        } else throw new NotFoundException("Can not found brand with id: " + id);
//    }
}
