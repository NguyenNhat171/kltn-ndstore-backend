package com.example.officepcstore.payload.response;

import com.example.officepcstore.models.enums.EnumGender;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String email;
    private String name;
    private String avatar;
    private String phone;
    private Integer province;
    private Integer district;
    private Integer ward;
    private String address;
    private EnumGender gender;
    private String role;
    private String state;
}
