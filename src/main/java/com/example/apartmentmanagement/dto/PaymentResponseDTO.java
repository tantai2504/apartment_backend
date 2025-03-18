package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String transactionId;
    private String paymentLinkId;
    private String qrCode;
    private String qrDataURL;
    private String webLinkURL;
    private String deeplink;
    private String status;
}