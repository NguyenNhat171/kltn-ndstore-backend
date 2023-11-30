package com.example.officepcstore.controllers;

import com.example.officepcstore.service.AdminControlService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AdminSearchController {
    private final AdminControlService adminControlService;
    @GetMapping(path = "/admin/manage/categories/to/search/control/all")
    public ResponseEntity<?> manageCategoryByAdmin (@RequestParam(value = "title", defaultValue = "") String title,
                                             @PageableDefault(size = 15, sort = "createDate", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable)
    {
        return adminControlService.searchCategoryNameInAdmin(title,pageable);
    }

    @GetMapping(path = "/admin/manage/brands/to/search/control/all")
    public ResponseEntity<?> manageBrandByAdmin (@RequestParam(value = "title", defaultValue = "") String title,
                                             @PageableDefault(size = 15, sort = "createDate", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable)
    {
        return adminControlService.searchBrandNameInAdmin(title,pageable);
    }

    @GetMapping(path = "/admin/manage/users/to/search/control/all")
    public ResponseEntity<?> manageUserByAdmin (@RequestParam(value = "accountMail", defaultValue = "") String accountMail,
                                                @RequestParam(value = "status", defaultValue = "") String status,
                                                @RequestParam(value = "role", defaultValue = "") String role,
                                                 @PageableDefault(size = 15, sort = "createDate", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable)
    {
        return adminControlService.searchUserByOption(accountMail,status,role,pageable);
    }

    @GetMapping(path = "/admin/manage/orders/to/search/control/all")
    public ResponseEntity<?> manageOrderByAdmin (@RequestParam(value = "customer", defaultValue = "") String customerName,
                                                 @RequestParam(value = "method", defaultValue = "") String paymentType,
                                                 @RequestParam(value = "beginDate", defaultValue = "") String beginDate,
                                                 @RequestParam(value = "endDate", defaultValue = "") String endDate,
                                                 @PageableDefault(size = 15, sort = "createDate", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable)
    {
        return adminControlService.searchFilterOrderAdminPage(customerName,paymentType,beginDate,endDate,pageable);

    }


    @GetMapping(path = "/admin/manage/products/to/search/control/all")
    public ResponseEntity<?> manageProductByAdmin (@RequestParam(value = "categoryId", defaultValue = "") String categoryId,
                                                   @RequestParam(value = "brandId", defaultValue = "") String brandId,
                                                   @RequestParam(value = "name", defaultValue = "") String name,
                                                 @PageableDefault(size = 15, sort = "createDate", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable)
    {
        return adminControlService.searchFilterProductAdminPage(categoryId,brandId,name,pageable);
    }
}
