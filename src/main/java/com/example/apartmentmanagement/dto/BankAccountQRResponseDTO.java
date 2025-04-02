package com.example.apartmentmanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccountQRResponseDTO {
    private Long userId;
    private String accountNumber;
    private String accountName;
    private String bankName;
    private String bankBin;
    private String qrCodeContent;
    private String qrCodeBase64;
}