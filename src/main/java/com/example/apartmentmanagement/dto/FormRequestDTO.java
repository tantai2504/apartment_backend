package com.example.apartmentmanagement.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FormRequestDTO {
    private String formType;
    private String status;
    private Long apartmentId;
    private MultipartFile file;
}
