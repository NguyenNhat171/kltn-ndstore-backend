package com.example.officepcstore.controllers;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.payload.request.CreateShipReq;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.service.OrderService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@RequestMapping("/api")
@RestController
public class OrderController {
    private final JwtUtils jwtUtils;
    private final OrderService orderService;

    @GetMapping(path = "/admin/manage/orders/get/all")
    public ResponseEntity<?> findAll (@RequestParam(defaultValue = "") String state,
                                      @PageableDefault(size = 5, sort = "lastUpdateStateDate") @ParameterObject Pageable pageable){
        return orderService.findAll(state, pageable);
    }

    @GetMapping(path = "/admin/manage/orders/get/detail/{orderId}")
    public ResponseEntity<?> findOrderById (@PathVariable String orderId){
        return orderService.findOrderById(orderId);
    }

    @PutMapping(path = "/admin/manage/orders/complete/{orderId}")
    public ResponseEntity<?> changeStateConfirmDelivery (@PathVariable String orderId){
        return orderService.setStateConfirmDelivery(orderId);
    }
//    @PutMapping(path = "/admin/manage/orders/process/delivery/{orderId}")
//    public ResponseEntity<?> changeStateDelivery (@PathVariable String orderId){
//        return orderService.setStateProcessDelivery(orderId);
//    }
//    @PutMapping(path = "/manage/orders/complete/{orderId}")
//    public ResponseEntity<?> confirmCompleteOrderByAdmin (@PathVariable String orderId,
//                                          HttpServletRequest request){
//        return orderService.changeStateDone( orderId);
//    }

    @PostMapping(path = "/admin/manage/orders/ship/{orderId}")
    public ResponseEntity<?> createShipOrder (@RequestBody CreateShipReq req,
                                              @PathVariable String orderId){
        return orderService.createShip(req, orderId);
    }

    @GetMapping(path = "/orders/get/detail/{orderId}")
    public ResponseEntity<?> userFindDetailOrderByUserId (@PathVariable String orderId,
                                                HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
            return orderService.findOrderDetailByUserId(orderId, user.getId());

    }

    @GetMapping(path = "/orders/get/list/user")
    public ResponseEntity<?> userGetAllOrder (HttpServletRequest request,
                                              @PageableDefault(size = 5, sort = "invoiceDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable ){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
            return orderService.findAllOrderByUserId(user.getId(),pageable);
    }


//    @GetMapping(path = "/admin/manage/orders/get/all/no/cart")
//    public ResponseEntity<?> findAllOrderNoCart (@PageableDefault(size = 5, sort = "invoiceDate", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable){
//        return orderService.findAllNoCart( pageable);
//    }


    @PutMapping(path = "/orders/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder (@PathVariable String orderId,
                                          HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        return orderService.cancelOrder(orderId, user.getId());
    }
}
