package com.example.officepcstore.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class LoginReq {

    @Email(message = "Email invalidate")
    private String username;
    private String password;
    private String otp;

}
