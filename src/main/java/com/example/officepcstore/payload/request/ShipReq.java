package com.example.officepcstore.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipReq {
    private Integer service_type_id; //mã loại dịch vụ
    private Long province_id;
    private Long district_id;
    private Long to_district_id;  //Mã Quận Huyện người nhận hàng.
    private String to_ward_code;  //Mã Phường Xã người nhận hàng.
    private Long ward_code;
    private Long weight;
    private Long height;
}
