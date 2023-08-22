package com.example.officepcstore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminReq {
    private String name;
    private String email;
    private String password;
    private String phone;
    private Integer province;
    private Integer district;
    private Integer ward;
    private String address;
    private String gender;
    private String role;
}
