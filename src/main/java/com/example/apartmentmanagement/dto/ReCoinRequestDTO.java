package com.example.apartmentmanagement.dto;

import com.example.apartmentmanagement.enums.BankEnum;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReCoinRequestDTO {
    private Long userRequestId;
    private String bankNumber;
    private String bankName;
    private String bankPin;
    private String accountName;
    private String content;
    private Float amount;
}
