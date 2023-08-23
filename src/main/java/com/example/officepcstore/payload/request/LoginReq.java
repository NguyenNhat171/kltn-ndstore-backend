package com.example.officepcstore.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class LoginReq {
    @Email(message = "Email invalidate")
    private String email;
    private String password;
}
