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

    public ResponseEntity<?> searchBrandNameInAdmin(String nameBrand,String state,Pageable pageable) {
        Page<Brand> brandResult;
        if(!nameBrand.isBlank() && state.isBlank())
        {
            brandResult =brandRepository.findAllByNameLikeIgnoreCase(nameBrand, pageable);

        }
        else if(!nameBrand.isBlank() && !state.isBlank()){
            brandResult = brandRepository.findAllByDisplayAndNameLikeIgnoreCase(state,nameBrand,pageable);
        }
        else if(!state.isBlank() && nameBrand.isBlank())
        {
             brandResult= brandRepository.findAllByDisplay(state,pageable);
        }
        else {
            brandResult  = brandRepository.findAll(pageable);
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


    public ResponseEntity<?> searchCategoryNameInAdmin(String nameCategory,String state,Pageable pageable) {
        Page<Category> resultPageCategory;
        if(nameCategory.isBlank() && state.isBlank())
        {
            resultPageCategory  = categoryRepository.findAll(pageable);
        }
        else if(!state.isBlank() && nameCategory.isBlank())
        {
            resultPageCategory =categoryRepository.findAllByDisplay(state,pageable);
        }
        else if(!state.isBlank() && !nameCategory.isBlank())
        { resultPageCategory = categoryRepository.findAllByTitleCategoryLikeIgnoreCaseAndDisplay(nameCategory,state, pageable);}
        else  {
            resultPageCategory = categoryRepository.findAllByTitleCategoryLikeIgnoreCase(nameCategory, pageable);
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

// email expect
    public ResponseEntity<?> searchUserByOption(String emailUser, String statusUser, String roleUser, Pageable pageable) {
        Page<User> pageFilterUser;
        Map<String,Object>  userHashMap = new HashMap<>();
        if(!emailUser.isBlank())
        {
            pageFilterUser = userRepository.findAllByEmailLikeIgnoreCase(emailUser,pageable);
        }
        else if (!statusUser.isBlank() &&emailUser.isBlank()) {
            if(roleUser.isBlank())
            pageFilterUser = userRepository.findAllByStatusUser(statusUser, pageable);
            else
                pageFilterUser = userRepository.findAllByStatusUserAndRole(statusUser,roleUser,pageable);
        }
        else if(!roleUser.isBlank()&& statusUser.isBlank()){
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not Found any account", ""));
    }


//    public ResponseEntity<?> searchFilterProductAdminPage(String categoryProId, String brandProId, String productName,String status, Pageable pageable) {
//        Page<Product> getProductResultPage;
//        Map<String,Object>  productPageMap = new HashMap<>();
//        if (!categoryProId.isBlank()) {
//            if(brandProId.isBlank()  && status.isBlank()) {
//                getProductResultPage = productRepository.findAllByCategory_Id(new ObjectId(categoryProId), pageable);
//            }
//            else if(!status.isBlank())
//            {
//                getProductResultPage = productRepository.findAllByCategory_IdAndState(new ObjectId(categoryProId),status,pageable);
//            }
//            else
//                getProductResultPage= productRepository.findAllByCategory_IdAndBrand_IdAndState(new ObjectId(categoryProId),new ObjectId(brandProId),status,pageable);
//        }
//        else if (!brandProId.isBlank())
//        {
//            if(categoryProId.isBlank() &&status.isBlank()) {
//                getProductResultPage = productRepository.findAllByBrand_Id(new ObjectId(brandProId), pageable);
//            }
//            else
//                getProductResultPage =productRepository.findAllByBrand_IdAndState(new ObjectId(brandProId),status,pageable);
//
//        }
//        else if(!productName.isBlank()&& categoryProId.isBlank()&& brandProId.isBlank()){
//            getProductResultPage = productRepository.findAllBy(TextCriteria
//                    .forDefaultLanguage().matchingAny(productName),pageable);
//        }
//        else if(!status.isBlank())
//        {
//            getProductResultPage = productRepository.findAllByState(status,pageable);
//        }
//        else {
//            getProductResultPage = productRepository.findAll(pageable);
//        }
//        List<AllProductResponse>productResponses = getProductResultPage.stream().map(productMap::toGetAllProductRes).collect(Collectors.toList());
//        productPageMap.put("list",productResponses);
//        productPageMap.put("totalQuantity", getProductResultPage.getTotalElements());
//        productPageMap.put("totalPage", getProductResultPage.getTotalPages());
//        if (productResponses.size() > 0)
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Get all product success",productPageMap));
//        else
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObjectData(false, "Not Found any product", ""));
//    }


    public ResponseEntity<?> searchUpdateFilterProductAdminPage(String categoryProId, String brandProId, String productName, Pageable pageable) {
        Page<Product> getProductResultPage;
        Map<String,Object>  productPageMap = new HashMap<>();
        if (!categoryProId.isBlank()) {
            if(brandProId.isBlank()  && productName.isBlank()) {
                getProductResultPage = productRepository.findAllByCategory_Id(new ObjectId(categoryProId), pageable);
            }
            else if(!productName.isBlank() && brandProId.isBlank())
            {
                getProductResultPage = productRepository.findAllByCategory_IdAndNameLikeIgnoreCase(new ObjectId(categoryProId),productName,pageable);
            }
            else
                getProductResultPage= productRepository.findAllByCategory_IdAndBrand_IdAndNameLikeIgnoreCase(new ObjectId(categoryProId),new ObjectId(brandProId),productName,pageable);
        }
        else if (!brandProId.isBlank())
        {
            if(categoryProId.isBlank() &&productName.isBlank()) {
                getProductResultPage = productRepository.findAllByBrand_Id(new ObjectId(brandProId), pageable);
            }
            else
                getProductResultPage =productRepository.findAllByBrand_IdAndNameLikeIgnoreCase(new ObjectId(brandProId),productName,pageable);

        }
        else if(!productName.isBlank()&& categoryProId.isBlank()&& brandProId.isBlank()){
//            getProductResultPage = productRepository.findAllBy(TextCriteria
//                    .forDefaultLanguage().matchingAny(productName),pageable);
            getProductResultPage = productRepository.findAllByNameLikeIgnoreCase(productName,pageable);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not Found any product", ""));
    }


    public ResponseEntity<?> sortProductSoldOrPriceAdminPage( String optionSoldSort,String optionPriceSort,Pageable pageable) {
        Page<Product> getProductResultPage;
        Map<String,Object>  productPageMap = new HashMap<>();
        if (optionSoldSort.equals("asc") && optionPriceSort.isBlank()) {
            getProductResultPage = productRepository.findAllByOrderBySoldAsc(pageable);
        }
        else if (optionPriceSort.equals("asc") && optionSoldSort.isBlank()){
            getProductResultPage = productRepository.findAllByOrderByReducedPriceAsc(pageable);
        }
        else if (optionSoldSort.equals("desc")&& optionPriceSort.isBlank())
        {
                getProductResultPage = productRepository.findAllByOrderBySoldDesc(pageable);
        }
        else if (optionPriceSort.equals("desc")&& optionSoldSort.isBlank())
        {
            getProductResultPage = productRepository.findAllByOrderByReducedPriceDesc(pageable);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not Found any product", ""));
    }

//    public ResponseEntity<?> searchFilterOrderAdminPage(String customerName, String paymentType,String status, String beginDay,String endDay, Pageable pageable) {
//        Page<Order> getOrderResultPage;
//        LocalDateTime startDate = LocalDateTime.now();
//        LocalDateTime endDate = LocalDateTime.now();
//        String typeDate = "dd-MM-yyyy";
//        DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
//            if (!beginDay.isBlank()) startDate = LocalDate.parse(beginDay, df).atStartOfDay();
//            if (!endDay.isBlank()) endDate = LocalDate.parse(endDay, df).atStartOfDay();
//        if (!customerName.isBlank()) {
//            if(!paymentType.isBlank()&&status.isBlank()&& beginDay.isBlank() && endDay.isBlank())
//            {
//                getOrderResultPage = orderRepository.findAllByPaymentOrderMethodAndUser_NameLike(paymentType,customerName,pageable);
//            }
//            else if(paymentType.isBlank()&&status.isBlank()&& !beginDay.isBlank() && !endDay.isBlank())
//            {
//                getOrderResultPage = orderRepository.findAllByUser_NameLikeAndInvoiceDateBetween(customerName,startDate,endDate,pageable);
//            }
//            else if(paymentType.isBlank()&&!status.isBlank()&&beginDay.isBlank() && endDay.isBlank())
//            {
//                getOrderResultPage = orderRepository.findAllByStatusOrderAndUser_NameLike(status,customerName,pageable);
//            }
//            else if(!paymentType.isBlank()&&!status.isBlank()&& beginDay.isBlank() && endDay.isBlank())
//            {
//                getOrderResultPage = orderRepository.findAllByPaymentOrderMethodAndStatusOrderAndUser_NameLike(paymentType,status,customerName,pageable);
//            }
//            else if(!paymentType.isBlank()&&!status.isBlank()&& !beginDay.isBlank() && !endDay.isBlank()){
//            getOrderResultPage=orderRepository.findAllByPaymentOrderMethodAndStatusOrderAndUser_NameLikeAndInvoiceDateBetween(paymentType,status,customerName,startDate,endDate,pageable);
//            }
//            else {
//                getOrderResultPage = orderRepository.findAllByUser_NameLike(customerName, pageable);
//            }
//        } else if (!paymentType.isBlank()) {
//            if(customerName.isBlank() && status.isBlank() &&beginDay.isBlank() && endDay.isBlank())
//            {
//            getOrderResultPage = orderRepository.findAllByPaymentOrderMethodAndInvoiceDateBetween(paymentType,startDate,endDate,pageable);
//            }
//            else if(customerName.isBlank() && !status.isBlank() &&beginDay.isBlank() && endDay.isBlank())
//            {
//                getOrderResultPage = orderRepository.findAllByPaymentOrderMethodAndStatusOrder(paymentType,status,pageable);
//            }
//            else {
//                getOrderResultPage = orderRepository.findAllByPaymentOrderMethod(paymentType, pageable);
//            }
//        }
//        else if(!beginDay.isBlank() &&customerName.isBlank() && paymentType.isBlank() &&status.isBlank()){
//            getOrderResultPage = orderRepository.findAllByInvoiceDateBetween(startDate,endDate,pageable);
//        }
//        else if(!status.isBlank()  && paymentType.isBlank() &&customerName.isBlank())
//        {
//            if(!beginDay.isBlank() &&paymentType.isBlank() &&customerName.isBlank()){
//                getOrderResultPage =orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate,endDate,status,pageable);
//            }
//            else
//            getOrderResultPage = orderRepository.findAllByStatusOrder(status,pageable);
//        }
//        else {
//           getOrderResultPage = orderRepository.findAll(pageable);
//        }
//        Map<String,Object>  orderPageMap = new HashMap<>();
//        List<OrderResponse>orderResponses = getOrderResultPage.stream().map(orderMap::getOrderDetailResponse).collect(Collectors.toList());
//        orderPageMap.put("list", orderPageMap);
//        orderPageMap.put("totalQuantity", getOrderResultPage.getTotalElements());
//        orderPageMap.put("totalPage", getOrderResultPage.getTotalPages());
//        if (orderResponses.size() > 0)
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Get order success",orderPageMap));
//        else
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObjectData(false, "Not Found any order", ""));
//    }

//    public ResponseEntity<?> searchFilterOrderAdminPage(String customerId,String status, String beginDay,String endDay, Pageable pageable) {
//
//        Page<Order> getOrderResultPage;
//
//    LocalDateTime startDate = LocalDateTime.now();
//    LocalDateTime endDate = LocalDateTime.now();
//    String typeDate = "dd-MM-yyyy";
//    DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
//    if (!beginDay.isBlank()) startDate = LocalDate.parse(beginDay, df).atStartOfDay();
//    if (!endDay.isBlank()) endDate = LocalDate.parse(endDay, df).atStartOfDay();
//
//        if (!customerId.isBlank()) {
//           if(status.isBlank()&&beginDay.isBlank() && endDay.isBlank()) {
//               getOrderResultPage = orderRepository.findOrdersByUser_IdAndStatusOrderNot(new ObjectId(customerId), Constant.ORDER_CART, pageable);
//           }
//           else if(!status.isBlank()&&beginDay.isBlank() && endDay.isBlank()){
//               getOrderResultPage =orderRepository.findAllByStatusOrderAndUser_Id(status,new ObjectId(customerId),pageable);
//           }
//               else if(status.isBlank()&&!beginDay.isBlank() && !endDay.isBlank())
//           {
//               getOrderResultPage=orderRepository.findAllByUser_IdAndInvoiceDateBetween(new ObjectId(customerId),startDate,endDate,pageable);
//           }
//            else {
//               getOrderResultPage = orderRepository.findAllByStatusOrderAndUser_IdAndInvoiceDateBetween(status, new ObjectId(customerId), startDate, endDate, pageable);
//           }
//        }
//        else if(!status.isBlank() &&customerId.isBlank())
//        {
//            if( !beginDay.isBlank() && !endDay.isBlank()){
//                getOrderResultPage =orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate,endDate,status,pageable);
//            }
//            else {
//                getOrderResultPage = orderRepository.findAllByStatusOrder(status, pageable);
//            }
//        }
//        else if(status.isBlank() &&customerId.isBlank()&&!beginDay.isBlank() && !endDay.isBlank()){
//            getOrderResultPage =orderRepository.findAllByInvoiceDateBetween(startDate,endDate,pageable);
//        }
//        else {
//            getOrderResultPage = orderRepository.findAll(pageable);
//        }
//        Map<String,Object>  orderPageMap = new HashMap<>();
//        List<OrderResponse>orderResponses = getOrderResultPage.stream().map(orderMap::getOrderDetailResponse).collect(Collectors.toList());
//        orderPageMap.put("list", orderPageMap);
//        orderPageMap.put("totalQuantity", getOrderResultPage.getTotalElements());
//        orderPageMap.put("totalPage", getOrderResultPage.getTotalPages());
//        if (orderResponses.size() > 0)
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObjectData(true, "Get order success",orderPageMap));
//        else
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObjectData(false, "Not Found any order", ""));
//    }


    public ResponseEntity<?> searchFilterOrderAdminPage(String customerId,String status, String beginDay,String endDay, Pageable pageable)
    {
        Page<Order> getOrderResultPage;
         if(!beginDay.isBlank() &&!endDay.isBlank()) {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now();
            String typeDate = "dd-MM-yyyy";
            DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
             if (!beginDay.isBlank())  startDate = LocalDate.parse(beginDay, df).atStartOfDay();
             if (!endDay.isBlank())  endDate = LocalDate.parse(endDay, df).atStartOfDay();
             if(status.isBlank() && !customerId.isBlank()){
                 getOrderResultPage=orderRepository.findAllByUser_IdAndInvoiceDateBetween(new ObjectId(customerId),startDate,endDate,pageable);
             }
             else if(!status.isBlank() &&customerId.isBlank()){
                 getOrderResultPage =orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate,endDate,status,pageable);
             }
             else if(!status.isBlank() &&!customerId.isBlank()){
                 getOrderResultPage =orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate,endDate,status,pageable);
             }
             else if(!status.isBlank() &&!customerId.isBlank()){
                 getOrderResultPage = orderRepository.findAllByStatusOrderAndUser_IdAndInvoiceDateBetween(status, new ObjectId(customerId), startDate, endDate, pageable);
             }
             else{
                 getOrderResultPage = orderRepository.findAllByInvoiceDateBetweenAndStatusOrderNot(startDate,endDate,Constant.ORDER_CART,pageable);
                 }
         }
       else if (!customerId.isBlank()) {
            if(status.isBlank()&&beginDay.isBlank() && endDay.isBlank()) {
                getOrderResultPage = orderRepository.findOrdersByUser_IdAndStatusOrderNot(new ObjectId(customerId), Constant.ORDER_CART, pageable);
            }
            else {
                getOrderResultPage =orderRepository.findAllByStatusOrderAndUser_Id(status,new ObjectId(customerId),pageable);
            }

        }
        else if(!status.isBlank() &&customerId.isBlank()&&beginDay.isBlank() && endDay.isBlank())
        {
                getOrderResultPage = orderRepository.findAllByStatusOrder(status, pageable);
        }

        else {
            getOrderResultPage = orderRepository.findAll(pageable);
        }
        Map<String,Object>  orderPageMap = new HashMap<>();
        List<OrderResponse>orderResponses = getOrderResultPage.stream().map(orderMap::getOrderDetailResponse).collect(Collectors.toList());
        orderPageMap.put("list", orderResponses);
        orderPageMap.put("totalQuantity", getOrderResultPage.getTotalElements());
        orderPageMap.put("totalPage", getOrderResultPage.getTotalPages());
        if (orderResponses.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success",orderPageMap));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not Found any order", ""));
    }

    public ResponseEntity<?> searchFilterOrderAdminPageAnother(String customerName,String status, String beginDay,String endDay, Pageable pageable)
    {
        Page<Order> getOrderResultPage;
        if(!beginDay.isBlank() &&!endDay.isBlank()) {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now();
            String typeDate = "dd-MM-yyyy";
            DateTimeFormatter df = DateTimeFormatter.ofPattern(typeDate);
            if (!beginDay.isBlank())  startDate = LocalDate.parse(beginDay, df).atStartOfDay();
            if (!endDay.isBlank())  endDate = LocalDate.parse(endDay, df).atStartOfDay();
            if(status.isBlank() && !customerName.isBlank()){
                getOrderResultPage=orderRepository.findAllByShipment_CustomerNameLikeIgnoreCaseAndInvoiceDateBetweenAndStatusOrderNot(customerName,startDate,endDate,Constant.ORDER_CART,pageable);
            }
            else if(!status.isBlank() &&customerName.isBlank()){
                getOrderResultPage =orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate,endDate,status,pageable);
            }
            else if(!status.isBlank() &&!customerName.isBlank()){
                getOrderResultPage =orderRepository.findAllByInvoiceDateBetweenAndStatusOrder(startDate,endDate,status,pageable);
            }
            else if(!status.isBlank() &&!customerName.isBlank()){
                getOrderResultPage = orderRepository.findAllByStatusOrderAndShipment_CustomerNameAndInvoiceDateBetween(status, customerName, startDate, endDate, pageable);
            }
            else{
                getOrderResultPage = orderRepository.findAllByInvoiceDateBetweenAndStatusOrderNot(startDate,endDate,Constant.ORDER_CART,pageable);
            }
        }
        else if (!customerName.isBlank()) {
            if(status.isBlank()&&beginDay.isBlank() && endDay.isBlank()) {
                getOrderResultPage = orderRepository.findOrdersByShipment_CustomerNameLikeIgnoreCaseAndStatusOrderNot(customerName, Constant.ORDER_CART, pageable);
            }

            else {
                getOrderResultPage =orderRepository.findAllByStatusOrderAndShipment_CustomerNameLikeIgnoreCase(status,customerName,pageable);
            }

        }
        else if(!status.isBlank() &&customerName.isBlank()&&beginDay.isBlank() && endDay.isBlank())
        {
            getOrderResultPage = orderRepository.findAllByStatusOrder(status, pageable);
        }

        else {
            getOrderResultPage = orderRepository.findAllByStatusOrderNot(Constant.ORDER_CART,pageable);
        }
        Map<String,Object>  orderPageMap = new HashMap<>();
        List<OrderResponse>orderResponses = getOrderResultPage.stream().map(orderMap::getOrderDetailResponse).collect(Collectors.toList());
        orderPageMap.put("list", orderResponses);
        orderPageMap.put("totalQuantity", getOrderResultPage.getTotalElements());
        orderPageMap.put("totalPage", getOrderResultPage.getTotalPages());
        if (orderResponses.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get order success",orderPageMap));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not Found any order", ""));
    }

    public ResponseEntity<?> searchUserByEmailOrPhone(String keyword, Pageable pageable)
    {
        Page<User> pageFilterUser;
        Map<String,Object>  userHashMap = new HashMap<>();
            pageFilterUser=userRepository.findAllBy(TextCriteria.forDefaultLanguage().matchingAny(keyword), pageable);
        List<UserResponse> userSearch = pageFilterUser.stream().map(userMap::toUserRes).collect(Collectors.toList());
        userHashMap.put("allPage", pageFilterUser.getTotalPages());
        userHashMap.put("allQuantity", pageFilterUser.getTotalElements());
        userHashMap.put("listUser", userSearch);
        if (userSearch.size() > 0)
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObjectData(true, "Get all account success", userHashMap));
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObjectData(false, "Not Found any account", ""));
        //            getProductResultPage = productRepository.findAllBy(TextCriteria
//                    .forDefaultLanguage().matchingAny(productName),pageable);
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
