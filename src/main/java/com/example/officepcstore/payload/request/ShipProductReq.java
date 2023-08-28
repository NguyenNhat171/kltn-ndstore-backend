package com.example.officepcstore.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipProductReq {
    private String name;
    private int quantity;
}
