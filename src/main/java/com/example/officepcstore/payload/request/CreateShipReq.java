package com.example.officepcstore.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class CreateShipReq {
    private String to_province_name;
    private String to_district_name;
    private String to_ward_name;
    private List<ShipProductReq> items = new ArrayList<>();
}
