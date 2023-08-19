package com.example.officepcstore.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseObjectData {
    private boolean Success;
    private String message;
    private Object data;
}
