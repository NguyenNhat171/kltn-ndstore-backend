package com.example.officepcstore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProviderReq {
    private String name;
    private String email;
    private String avatar;

}
