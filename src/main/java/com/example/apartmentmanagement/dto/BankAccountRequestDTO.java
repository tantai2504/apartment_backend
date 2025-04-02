package com.example.apartmentmanagement.dto;

import com.example.apartmentmanagement.enums.BankEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountRequestDTO {
    private Long userId;
    private String accountNumber;
    private String accountName;
    private BankEnum bank;
    private float amount;
    private String content;
    private Long createdUserId;
}