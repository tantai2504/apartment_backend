package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyUserDTO {
    private String verificationFormName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private LocalDateTime contractStartDate;

    private LocalDateTime contractEndDate;

    private List<MultipartFile> imageFile;
}
