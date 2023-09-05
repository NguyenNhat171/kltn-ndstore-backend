package com.example.officepcstore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterReq {
    private String name;
    private String email;
    private String password;
    private String phone;
    private Integer province;
    private Integer district;
    private Integer ward;
    private String address;
    private String role;
}
