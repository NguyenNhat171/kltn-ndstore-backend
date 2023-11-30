package com.example.officepcstore.controllers;

import com.example.officepcstore.excep.AppException;
import com.example.officepcstore.payload.request.LoginReq;
import com.example.officepcstore.payload.request.ProviderReq;
import com.example.officepcstore.payload.request.RegisterReq;
import com.example.officepcstore.payload.request.VerifyReq;
import com.example.officepcstore.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AccountController {
    private final AccountService accountService;
    @PostMapping("/login/account")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq loginRequest) {
        return accountService.loginAccount(loginRequest);
    }

    @PostMapping("/register/account")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq registerRequest) {
        return accountService.registerAccount(registerRequest);
    }

    @PostMapping("mail/forget/pass/account") //User OTP Get Token Reset Pass
    public ResponseEntity<?> getOTPResetForgetPass(@RequestParam(value ="email")String email)
    {
        if (!email.isBlank())
            return accountService.sendMailResetForgetPass(email);
        throw new AppException(HttpStatus.BAD_REQUEST.value(), "Email is required");
    }
//    @PostMapping("/social/account")
//    public ResponseEntity<?> registerSocial(@RequestBody ProviderReq providerReq) {
//        return accountService.loginProvider(providerReq);
//    }

    @PostMapping("/verifyaccount/account")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyReq req) {
        return accountService.verifyOTP(req);
    }
    @PostMapping("/mail/get/otp/account")
    public ResponseEntity<?> getOTPMail(@RequestParam  (value ="email")String email) {
        if (!email.isBlank()) return accountService.sendMailGetOTP(email);
        throw new AppException(HttpStatus.BAD_REQUEST.value(), "Email is required");
    }
}
