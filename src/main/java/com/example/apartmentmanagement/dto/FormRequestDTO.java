package com.example.apartmentmanagement.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FormRequestDTO {
    private String formType;
    private String status;
    private String reason;
    private String apartmentName;
    private MultipartFile file;

    public FormRequestDTO(String formType, String reason, String apartmentName, MultipartFile file) {
        this.formType = formType;
        this.reason = reason;
        this.apartmentName = apartmentName;
        this.file = file;
    }
}
