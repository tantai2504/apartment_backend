package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyUserRequestDTO {
    private String verificationFormName;

    private int verificationFormType;

    private String apartmentName;

    private String email;

    private String phoneNumber;

    private LocalDateTime contractStartDate;

    private LocalDateTime contractEndDate;

    public VerifyUserRequestDTO(LocalDateTime contractStartDate, LocalDateTime contractEndDate) {
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
    }
}
