package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.Payment;

public interface PaymentService {
    Payment processPayment(Long billId, String username);
}
