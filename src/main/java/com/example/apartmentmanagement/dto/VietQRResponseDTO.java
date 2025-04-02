package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VietQRResponseDTO {
    private String bin;           // Mã ngân hàng
    private String accountNumber; // Số tài khoản
    private String accountName;   // Tên chủ tài khoản
    private double amount;        // Số tiền
    private String description;   // Nội dung
    private String qrCodeContent; // Nội dung QR
    private String qrCodeBase64;  // Ảnh QR base64
}
