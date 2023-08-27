package com.example.officepcstore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartReq {
    @NotBlank(message = "Product option id is required")
    private String productId;
    @NotNull(message = "Quantity is required")
    private long quantity;
}
