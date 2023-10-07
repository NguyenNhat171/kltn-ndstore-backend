package com.example.officepcstore.service;


import com.example.officepcstore.config.CloudinaryConfig;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.BrandMap;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.models.enity.Category;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.request.BrandReq;
import com.example.officepcstore.payload.response.BrandResponse;
import com.example.officepcstore.payload.response.OrderResponse;
import com.example.officepcstore.repository.BrandRepository;
import com.mongodb.MongoWriteException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final CloudinaryConfig cloudinary;
    private final BrandMap brandMap;


    public ResponseEntity<ResponseObjectData> findAll() {
        List<Brand> list = brandRepository.findAllByState(Constant.ENABLE);
        if (list.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all brand success", list));
        throw new NotFoundException("Can not found any brand");
    }

    public ResponseEntity<ResponseObjectData> findAllByAdmin(String state,Pageable pageable) {
        Page<Brand> listBrand;
        if(state == null || state.isBlank())
             listBrand = brandRepository.findAll(pageable);
        else
            listBrand = brandRepository.findAllByState(state, pageable);
       // Page<Brand> listBrand = brandRepository.findAll(pageable);
        List<BrandResponse> brandResList = listBrand.stream().map(brandMap::getBrandResponse).collect(Collectors.toList());
        Map<String, Object> brandResp = new HashMap<>();
        brandResp.put("totalPage", listBrand.getTotalPages());
        brandResp.put("totalBrand", listBrand.getTotalElements());
        brandResp.put("listBrand",brandResList);
        if (brandResList.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all brand success", brandResp));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not Found any Brand", ""));
    }




    public ResponseEntity<?> findBrandByIdInUser(String id) {
        Optional<Brand> brand = brandRepository.findBrandByIdAndState(id, Constant.ENABLE);
        if (brand.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get brand success", brand));
        throw new NotFoundException("Can not found brand with id: " + id);
    }
    public ResponseEntity<?> findBrandByIdInAdmin(String id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get brand success", brand));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not found brand"+id,""));
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
    public ResponseEntity<?> updateDetailBrand(String id, BrandReq brandReq) {
        Optional<Brand> brandFound = brandRepository.findById(id);
        if (brandFound.isPresent()) {
            brandFound.get().setName(brandReq.getName());
            brandFound.get().setState(brandReq.getState());
            brandRepository.save(brandFound.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Update Brand complete", brandFound));
        }
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not found brand" +id, ""));
    }

    @Transactional
    public ResponseEntity<?> updateBrandImage(String id, MultipartFile file) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            if (file != null && !file.isEmpty()) {
                try {
                    String imgUrl = cloudinary.uploadImage(file, brand.get().getImageBrand());
                    brand.get().setImageBrand(imgUrl);
                    brandRepository.save(brand.get());
                } catch (IOException e) {
                    throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload image");
                }
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Update image complete", brand));
            }
        }
        throw new NotFoundException("Can not found brand with id: " + id);
    }


    @Transactional
        public ResponseEntity<?> changeStateDisableBrand (String id){
            Optional<Brand> brand = brandRepository.findById(id);
            if (brand.isPresent()) {
                if (!brand.get().getDependentProducts().isEmpty())
                    throw new AppException(HttpStatus.CONFLICT.value(),
                        "Product exist");
                brand.get().setState(Constant.DISABLE);
                brandRepository.save(brand.get());
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObjectData(true, "Block brand success with id: " + id, ""));
            } else  return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not found Brand " + id, ""));
        }

    @Transactional
    public ResponseEntity<?> changeStateEnableBrand (String id){
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent() &&brand.get().getState().equals(Constant.DISABLE)) {
            brand.get().setState(Constant.ENABLE);
            brandRepository.save(brand.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Enable brand success with id: " + id, ""));
        } else  return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObjectData(false, "Not found Brand " + id, ""));
    }

}
