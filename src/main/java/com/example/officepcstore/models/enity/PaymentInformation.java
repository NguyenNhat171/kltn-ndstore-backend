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
//paymentdetails
public class PaymentInformation {
    private String paymentId;
    private String paymentType;
    private String paymentToken;
    private Map<String, Object> paymentInfo = new HashMap<>();
}
