package com.example.officepcstore.controllers;

import com.example.officepcstore.config.Constant;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.payload.request.CreateShipReq;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.service.OrderService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@RequestMapping("/api")
@RestController
public class OrderController {
    private final JwtUtils jwtUtils;
    private final OrderService orderService;

    @GetMapping(path = "/manage/orders/get")
    public ResponseEntity<?> findAll (@RequestParam(defaultValue = "") String state,
                                      @PageableDefault(size = 5, sort = "lastModifiedDate") @ParameterObject Pageable pageable){
        return orderService.findAll(state, pageable);
    }

    @GetMapping(path = "/manage/orders/{orderId}")
    public ResponseEntity<?> findOrderById (@PathVariable String orderId){
        return orderService.findOrderById(orderId);
    }

    @PutMapping(path = "/manage/orders/complete/{orderId}")
    public ResponseEntity<?> changeStateConfirmDelivery (@PathVariable String orderId){
        return orderService.setStateConfirmDelivery(orderId);
    }
    @PutMapping(path = "/manage/orders/process/delivery/{orderId}")
    public ResponseEntity<?> changeStateDelivery (@PathVariable String orderId){
        return orderService.setStateProcessDelivery(orderId);
    }
//    @PutMapping(path = "/manage/orders/complete/{orderId}")
//    public ResponseEntity<?> confirmCompleteOrderByAdmin (@PathVariable String orderId,
//                                          HttpServletRequest request){
//        return orderService.changeStateDone( orderId);
//    }

    @PostMapping(path = "/manage/orders/ship/{orderId}")
    public ResponseEntity<?> createShipOrder (@RequestBody CreateShipReq req,
                                              @PathVariable String orderId){
        return orderService.createShip(req, orderId);
    }

    @GetMapping(path = "/orders/{orderId}")
    public ResponseEntity<?> userFindOrderByUserId (@PathVariable String orderId,
                                                HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
            return orderService.findOrderByUserId(orderId, user.getId());

    }

    @PutMapping(path = "/orders/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder (@PathVariable String orderId,
                                          HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        return orderService.cancelOrder(orderId, user.getId());
    }
}
