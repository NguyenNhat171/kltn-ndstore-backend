package com.example.officepcstore.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResetForgetPassReq {
    private String newPass;
}
