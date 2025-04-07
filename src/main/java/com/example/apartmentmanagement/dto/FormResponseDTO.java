package com.example.apartmentmanagement.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FormResponseDTO {
    private Long formId;
    private String formType;
    private String fileUrl;
    private String fileName;
    private Date createdAt;
    private Date executedAt;
    private String status;
    private Long userId;
    private Long apartmentId;
}
