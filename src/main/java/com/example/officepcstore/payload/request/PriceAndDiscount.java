package com.example.officepcstore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceAndDiscount {
    private String id;
    private BigDecimal price = null;
   // @Range(min = -1, max = 100, message = "Invalid discount! Only from 0 to 100")
    private int discount = -1;
}
