package com.example.officepcstore.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInventoryResponse {
    private String id;
    private String name;
    private long salable=0;
}
