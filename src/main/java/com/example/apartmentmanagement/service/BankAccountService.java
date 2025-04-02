package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.BankAccountQRResponseDTO;
import com.example.apartmentmanagement.dto.BankAccountRequestDTO;

public interface BankAccountService {
    BankAccountQRResponseDTO addBankAccountWithQR(BankAccountRequestDTO bankAccountRequestDTO);
    BankAccountQRResponseDTO generateQR(BankAccountRequestDTO bankAccountRequestDTO);
}
