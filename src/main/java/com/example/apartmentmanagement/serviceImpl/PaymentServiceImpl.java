package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.PaymentHistoryResponseDTO;
import com.example.apartmentmanagement.entities.Payment;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.repository.PaymentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<PaymentHistoryResponseDTO> getPaymentHistory(Long userId, int month, int year) {

        List<PaymentHistoryResponseDTO> paymentHistoryResponseDTOList = new ArrayList<>();

        PaymentHistoryResponseDTO paymentHistoryResponseDTO = new PaymentHistoryResponseDTO();



        return paymentHistoryResponseDTOList;
    }
}
