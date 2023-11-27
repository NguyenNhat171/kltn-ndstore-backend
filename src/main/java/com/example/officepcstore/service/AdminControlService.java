package com.example.officepcstore.service;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.BrandMap;
import com.example.officepcstore.map.UserMap;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.response.AllProductResponse;
import com.example.officepcstore.payload.response.BrandResponse;
import com.example.officepcstore.payload.response.UserResponse;
import com.example.officepcstore.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminControlService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BrandMap brandMap;
    private final UserMap userMap;

    public ResponseEntity<?> searchBrandNameInAdmin(String nameBrand,Pageable pageable) {
        Page<Brand> brand = brandRepository.getAllBrandByNameDes(nameBrand,pageable);
        List<BrandResponse> brandResList = brand.stream().map(brandMap::getBrandResponse).collect(Collectors.toList());
        Map<String, Object> brandResp = new HashMap<>();
        brandResp.put("totalPage", brand.getTotalPages());
        brandResp.put("totalBrand", brand.getTotalElements());
        brandResp.put("listBrand",brandResList);
        if (!brand.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get brand success", brand));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Get brand success", ""));
    }


    public ResponseEntity<?> searchCategoryNameInAdmin(String nameBrand,Pageable pageable) {
        Page<Brand> category = brandRepository.getAllBrandByNameDes(nameBrand,pageable);
        if (!category.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get brand success", category));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Get brand success", ""));
    }


    public ResponseEntity<?> searchUserByOption(String emailUser, String statusUser, String roleUser, Pageable pageable) {
        Page<User> pageFilterUser;
        Map<String,Object>  userHashMap = new HashMap<>();
        if (!statusUser.isBlank()) {
            pageFilterUser = userRepository.findAllByStatusUser(statusUser, pageable);
        } else if (!emailUser.isBlank()) {
            pageFilterUser = userRepository.findUsersByEmail(emailUser,pageable);
        }
        else if(!roleUser.isBlank()){
            pageFilterUser = userRepository.findUserByRole(roleUser,pageable);
        }
        else {
            pageFilterUser = userRepository.findAll(pageable);
        }
        List<UserResponse> userSearch = pageFilterUser.stream().map(userMap::toUserRes).collect(Collectors.toList());

        userHashMap.put("allPage", pageFilterUser.getTotalPages());
        userHashMap.put("allQuantity", pageFilterUser.getTotalElements());
        userHashMap.put("listUser", userSearch);
        if (userSearch.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all brand success", userHashMap));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not Found any Brand", ""));
    }


//    public ResponseEntity<?> searchUserByOption(String emailUser, String statusUser, String roleUser, Pageable pageable) {
//        Page<User> pageFilterUser;
//        Map<String,Object>  userHashMap = new HashMap<>();
//        if (!statusUser.isBlank()) {
//            pageFilterUser = userRepository.findAllByStatusUser(statusUser, pageable);
//        } else if (!emailUser.isBlank()) {
//            pageFilterUser = userRepository.findUsersByEmail(emailUser,pageable);
//        }
//        else if(!roleUser.isBlank()){
//            pageFilterUser = userRepository.findUserByRole(roleUser,pageable);
//        }
//        else {
//            pageFilterUser = userRepository.findAll(pageable);
//        }
//        List<UserResponse> userSearch = pageFilterUser.stream().map(userMap::toUserRes).collect(Collectors.toList());
//
//        userHashMap.put("allPage", pageFilterUser.getTotalPages());
//        userHashMap.put("allQuantity", pageFilterUser.getTotalElements());
//        userHashMap.put("listUser", userSearch);
//        if (userSearch.size() > 0)
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Get all brand success", userHashMap));
//        else
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(false, "Not Found any Brand", ""));
//    }


//    private ResponseEntity<?> mapPageAndList(Page<> products, List<> resAll) //addPageableToRes
//    {
//        Map<String, Object> mapPage = new HashMap<>();
//        mapPage.put("list", resAll);
//        mapPage.put("totalQuantity", products.getTotalElements());
//        mapPage.put("totalPage", products.getTotalPages());
//        if (!resAll.isEmpty())
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "All product Success ", resp));
//        return null;
//    }
}
