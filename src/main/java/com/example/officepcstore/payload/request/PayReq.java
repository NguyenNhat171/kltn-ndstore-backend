package com.example.officepcstore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayReq {
    private String name;
    private String phone;
    private String address;
    private String province;
    private String district;
    private String ward;
//    private String fullAddress = "";
    private String note;
    private Long shipFee;
    private Integer serviceType;
    private Long estimatedTime = 0L; //expectedDeliveryTime
}
