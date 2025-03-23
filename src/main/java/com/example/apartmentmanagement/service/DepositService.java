package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.DepositRequestDTO;
import com.example.apartmentmanagement.dto.DepositResponseDTO;

public interface DepositService {
    DepositResponseDTO createDeposit(DepositRequestDTO depositRequestDTO);
}
