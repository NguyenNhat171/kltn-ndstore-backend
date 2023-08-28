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
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverDistrict;
    private String receiverWard;
    private String receiverAddress;
    private Map<String, Object> shipInfo = new HashMap<>(); //deliveryInfo

    public ShippingDetail(String receiverName, String receiverPhone, String receiverProvince, String receiverDistrict, String receiverWard, String receiverAddress) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverProvince = receiverProvince;
        this.receiverDistrict = receiverDistrict;
        this.receiverWard = receiverWard;
        this.receiverAddress = receiverAddress;
    }
}
