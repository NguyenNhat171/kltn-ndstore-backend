package com.example.officepcstore.models.enity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDetail {
    private String customerName;
    private String customerPhone;
    private String customerProvince;
    private String customerDistrict;
    private String customerWard;
    private String customerAddress;
    private Map<String, Object> shipInformation = new HashMap<>(); //deliveryInfo

//    public ShippingDetail(String receiverName, String receiverPhone, String receiverProvince, String receiverDistrict, String receiverWard, String receiverAddress) {
//        this.receiverName = receiverName;
//        this.receiverPhone = receiverPhone;
//        this.receiverProvince = receiverProvince;
//        this.receiverDistrict = receiverDistrict;
//        this.receiverWard = receiverWard;
//        this.receiverAddress = receiverAddress;
//    }
//

    public ShippingDetail(String customerName, String customerPhone, String customerProvince, String customerDistrict, String customerWard, String customerAddress) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerProvince = customerProvince;
        this.customerDistrict = customerDistrict;
        this.customerWard = customerWard;
        this.customerAddress = customerAddress;
    }
}
