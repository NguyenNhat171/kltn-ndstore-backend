package com.example.officepcstore.controllers;

import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.models.enity.User;
import com.example.officepcstore.payload.request.ChangePassReq;
import com.example.officepcstore.payload.request.ResetForgetPassReq;
import com.example.officepcstore.payload.request.UserReq;
import com.example.officepcstore.security.jwt.JwtUtils;
import com.example.officepcstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @GetMapping(path = "/admin/manage/users/get/all/list")
    public ResponseEntity<?> findAllUserByAdmin (@PageableDefault(size = 10, sort = "createdDate") @ParameterObject Pageable pageable){
        return userService.findAllUserByAdmin( pageable);
    }
    @PutMapping(path = "/users/edit/information/profile/{userId}")
    public ResponseEntity<?> updateUser ( @RequestBody UserReq req,
                                          @PathVariable("userId") String userId,
                                          HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId))
            return userService.updateProfileUser(userId, req);
        throw new AppException(HttpStatus.FORBIDDEN.value(),  "Not Found Token");
    }
    @PutMapping(path = "/users/change/new/password/{userId}")
    public ResponseEntity<?> changePasswordUser (@Valid @RequestBody ChangePassReq req,
                                                 @PathVariable("userId") String userId,
                                                 HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId))
            return userService.updatePassword(userId, req);
        throw new AppException(HttpStatus.FORBIDDEN.value(),  "Not Found Token");
    }

    @PutMapping(path = "/users/reset/new/password/{userId}")
    public ResponseEntity<?> updateResetPasswordUser ( @RequestBody ResetForgetPassReq resetPassRequest,
                                                       @PathVariable("userId") String userId,
                                                       HttpServletRequest request){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId))
            return userService.resetForgetPassword(userId,resetPassRequest);
        throw new AppException(HttpStatus.FORBIDDEN.value(),  "Not Found Token");
    }

    @GetMapping(path = "/users/get/profile/{userId}")
    public ResponseEntity<?> findUserById (@PathVariable("userId") String userId,
                                           HttpServletRequest request) {
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId))
            return userService.findUserById(userId);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "Not Found Token");
    }

    @GetMapping(path = "/admin/manage/users/get/check/profile/{userId}")
    public ResponseEntity<?> adminFindUserById (@PathVariable("userId") String userId) {
            return userService.findUserByAdmin(userId);
    }

    @PostMapping(path = "/users/update/avatar/{userId}")
    public ResponseEntity<?> updateAvatarUser (@PathVariable("userId") String userId,
                                         HttpServletRequest request,
                                         @RequestParam MultipartFile file){
        User user = jwtUtils.getUserFromJWT(jwtUtils.getJwtFromHeader(request));
        if (user.getId().equals(userId))
            return userService.updateUserAvatar(userId, file);
        throw new AppException(HttpStatus.FORBIDDEN.value(), "Not Found Token");
    }

    @PutMapping(path = "/admin/manage/users/change/state/{userId}")
    public ResponseEntity<?> changeStateUserByAdmin (@PathVariable("userId") String userId)
    {
            return userService.changeStateUserByAdmin(userId);
    }

}
