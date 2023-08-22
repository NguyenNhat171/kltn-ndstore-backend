package com.example.officepcstore.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerifyReq {
    private String otp;
    private String email;
    private String type;
}
