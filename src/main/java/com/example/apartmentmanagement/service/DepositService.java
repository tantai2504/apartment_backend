package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.DepositRequestDTO;
import com.example.apartmentmanagement.dto.DepositResponseDTO;

public interface DepositService {
    DepositResponseDTO processPaymentSuccess(DepositRequestDTO depositRequestDTO);

    DepositResponseDTO depositFlag(DepositRequestDTO depositRequestDTO);

    DepositResponseDTO cancel(DepositRequestDTO depositRequestDTO);
}
