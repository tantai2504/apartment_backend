package com.example.apartmentmanagement.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormDto {
    private String formType;
    private MultipartFile file;
}
