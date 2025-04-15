package com.example.apartmentmanagement.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FormRequestDTO {
    private String formType;
    private String status;
    private String reason;
    private Long apartmentId;
    private MultipartFile file;

    public FormRequestDTO(String formType, String reason, Long apartmentId, MultipartFile file) {
        this.formType = formType;
        this.reason = reason;
        this.apartmentId = apartmentId;
        this.file = file;
    }
}
