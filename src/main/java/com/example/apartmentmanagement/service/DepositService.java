package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.DepositListResponseDTO;
import com.example.apartmentmanagement.dto.DepositPaymentDTO;
import com.example.apartmentmanagement.dto.DepositRequestDTO;
import com.example.apartmentmanagement.dto.DepositResponseDTO;

import java.util.List;

public interface DepositService {

    DepositResponseDTO depositFlag(DepositRequestDTO depositRequestDTO);

    DepositResponseDTO processPaymentSuccess(DepositPaymentDTO depositPaymentDTO);

    DepositResponseDTO cancel(DepositPaymentDTO depositPaymentDTO);

    List<DepositListResponseDTO> getAllDeposits();
}
