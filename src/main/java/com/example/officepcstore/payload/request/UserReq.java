package com.example.officepcstore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReq {
    private String name;
    private String phone;
    private String address;
    private Integer province;
    private Integer district;
    private Integer ward;
    private String gender;
    private String password;
}
