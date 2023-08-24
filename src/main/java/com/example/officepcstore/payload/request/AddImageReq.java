package com.example.officepcstore.payload.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddImageReq {
    String image_Id;
    List<MultipartFile> files;
}
