package com.example.officepcstore.controllers;

import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.payload.request.LoginReq;
import com.example.officepcstore.payload.request.RegisterReq;
import com.example.officepcstore.payload.request.VerifyReq;
import com.example.officepcstore.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq loginRequest) {
        return authService.loginAccount(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq registerRequest) {
        return authService.registerAccount(registerRequest);
    }

    @PostMapping("/forgetpass")
    public ResponseEntity<?> resetForgetPass(@RequestBody VerifyReq req)
    {
        if (!req.getEmail().isBlank())
            return authService.resetForgetPass(req.getEmail());
        throw new AppException(HttpStatus.BAD_REQUEST.value(), "Email is required");
    }

    @PostMapping("/verifyaccount")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyReq req) {
        return authService.verifyOTP(req);
    }
    @PostMapping("/getotp")
    public ResponseEntity<?> getOTPMail(@RequestParam  (value ="email")String email) {
        if (!email.isBlank()) return authService.sendMailGetOTP(email);
        throw new AppException(HttpStatus.BAD_REQUEST.value(), "Email is required");
    }
//    @PostMapping("/getotp/reset")
//    public ResponseEntity<?> getOTPMailReset(@RequestParam(value ="email")String email) {
//        if (!email.isBlank()) return authService.sendMailResetGetOTP(email);
//        throw new AppException(HttpStatus.BAD_REQUEST.value(), "Email is required");
//    }

}
