package com.example.officepcstore.service;
import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.excep.NotFoundException;
import com.example.officepcstore.map.*;
import com.example.officepcstore.models.enity.Brand;
import com.example.officepcstore.models.enity.Category;
import com.example.officepcstore.models.enity.Order;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.models.enity.product.Product;
import com.example.officepcstore.payload.ResponseObjectData;
import com.example.officepcstore.payload.response.*;
import com.example.officepcstore.repository.*;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final ProductMap productMap;
    private final CategoryMap categoryMap;
    private final OrderMap orderMap;

    public ResponseEntity<?> searchBrandNameInAdmin(String nameBrand,Pageable pageable) {
        Page<Brand> brandResult;
        if(nameBrand.isBlank())
        {
            brandResult  = brandRepository.findAll(pageable);
        }
        else {
            brandResult =brandRepository.findAllByName(nameBrand, pageable);
        }
        List<BrandResponse> brandResList = brandResult.stream().map(brandMap::getBrandResponse).collect(Collectors.toList());
        Map<String, Object> brandResp = new HashMap<>();
        brandResp.put("totalPage", brandResult.getTotalPages());
        brandResp.put("totalBrand", brandResult.getTotalElements());
        brandResp.put("listBrand",brandResList);

        if (!brandResult.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get brand success", brandResp));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Get brand success", ""));
    }


    public ResponseEntity<?> searchCategoryNameInAdmin(String nameCategory,Pageable pageable) {
        Page<Category> resultPageCategory;
        if(nameCategory.isBlank())
        {
            resultPageCategory  = categoryRepository.findAll(pageable);
        }
        else  {
            resultPageCategory = categoryRepository.findAllByTitleCategory(nameCategory, pageable);
        }
        List<CategoryResponse> categoryResList =  resultPageCategory.stream().map(categoryMap::getCategoryResponse).collect(Collectors.toList());
        Map<String, Object> cateResp = new HashMap<>();
        cateResp.put("totalPage",  resultPageCategory.getTotalPages());
        cateResp.put("totalCategory",  resultPageCategory.getTotalElements());
        cateResp.put("listCategory",categoryResList);
        if (!resultPageCategory.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get brand success",cateResp));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Get brand success", ""));
    }


    public ResponseEntity<?> searchUserByOption(String emailUser, String statusUser, String roleUser, Pageable pageable) {
        Page<User> pageFilterUser;
        Map<String,Object>  userHashMap = new HashMap<>();
        if (!statusUser.isBlank() &&emailUser.isBlank()) {
            pageFilterUser = userRepository.findAllByStatusUser(statusUser, pageable);
        } else if (!emailUser.isBlank()&&statusUser.isBlank()) {
            pageFilterUser = userRepository.findUsersByEmail(emailUser,pageable);
        }
        else if(!roleUser.isBlank()&& statusUser.isBlank() && emailUser.isBlank()){
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
                    new ResponseObjectData(true, "Get all account success", userHashMap));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not Found any account", ""));
    }


    public ResponseEntity<?> searchFilterProductAdminPage(String categoryProId, String brandProId, String productName, Pageable pageable) {
        Page<Product> getProductResultPage;
        Map<String,Object>  productPageMap = new HashMap<>();
        if (!categoryProId.isBlank() && brandProId.isBlank()) {
            getProductResultPage = productRepository.findAllByCategory_Id(new ObjectId(categoryProId), pageable);
        } else if (!brandProId.isBlank()&& categoryProId.isBlank()) {
            getProductResultPage = productRepository.findAllByBrand_Id(new ObjectId(brandProId),pageable);
        }
        else if(!productName.isBlank()&& categoryProId.isBlank()&& brandProId.isBlank()){
            getProductResultPage = productRepository.findAllBy(TextCriteria
                    .forDefaultLanguage().matchingAny(productName),pageable);
        }
        else {
            getProductResultPage = productRepository.findAll(pageable);
        }
        List<AllProductResponse>productResponses = getProductResultPage.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
        productPageMap.put("list",productResponses);
        productPageMap.put("totalQuantity", getProductResultPage.getTotalElements());
        productPageMap.put("totalPage", getProductResultPage.getTotalPages());
        if (productResponses.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all product success",productPageMap));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(false, "Not Found any product", ""));
    }


    public ResponseEntity<?> searchFilterOrderAdminPage(String customerName, String paymentType, String beginDay,String endDay, Pageable pageable) {
        Page<Order> getOrderResultPage;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        String typeDate = "dd-MM-yyyy";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
            if (!beginDay.isBlank()) startDate = LocalDate.parse(beginDay, df).atStartOfDay();
            if (!endDay.isBlank()) endDate = LocalDate.parse(endDay, df).atStartOfDay();
        if (!customerName.isBlank()) {
            getOrderResultPage =orderRepository.findOrderByUser_Name(customerName, pageable);
        } else if (!paymentType.isBlank()) {
            getOrderResultPage = orderRepository.findOrderByPaymentOrderMethod(paymentType,pageable);
        }
        else if(!beginDay.isBlank()){
            getOrderResultPage = orderRepository.findAllByInvoiceDateBetween(startDate,endDate,pageable);
        }
        else {
           getOrderResultPage = orderRepository.findAll(pageable);
        }
        Map<String,Object>  orderPageMap = new HashMap<>();
        List<OrderResponse>orderResponses = getOrderResultPage.stream().map(orderMap::getOrderDetailResponse).collect(Collectors.toList());
        orderPageMap.put("list", orderPageMap);
        orderPageMap.put("totalQuantity", getOrderResultPage.getTotalElements());
        orderPageMap.put("totalPage", getOrderResultPage.getTotalPages());
        if (orderResponses.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success",orderPageMap));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not Found any order", ""));
    }

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
