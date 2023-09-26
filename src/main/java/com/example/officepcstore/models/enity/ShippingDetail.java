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
    private String customerNote;
    private Map<String, Object> serviceShipDetail = new HashMap<>(); //deliveryInfo



  public ShippingDetail(String customerName, String customerPhone, String customerProvince, String customerDistrict, String customerWard, String customerAddress) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerProvince = customerProvince;
        this.customerDistrict = customerDistrict;
        this.customerWard = customerWard;
        this.customerAddress = customerAddress;
    }

    public ShippingDetail(String customerName, String customerPhone, String customerProvince, String customerDistrict, String customerWard, String customerAddress, String customerNote) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerProvince = customerProvince;
        this.customerDistrict = customerDistrict;
        this.customerWard = customerWard;
        this.customerAddress = customerAddress;
        this.customerNote = customerNote;
    }
}
