package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VietQRRequestDTO {
    private String accountNumber; // Số tài khoản từ DB
    private double amount;        // Số tiền nhập từ màn hình
    private String description;   // Nội dung nhập từ màn hình
}
