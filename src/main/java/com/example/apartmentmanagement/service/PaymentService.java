package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.PaymentHistoryResponseDTO;
import com.example.apartmentmanagement.entities.Payment;

import java.util.List;

public interface PaymentService {
    List<PaymentHistoryResponseDTO> getPaymentHistory(Long userId, int month, int year);
}
