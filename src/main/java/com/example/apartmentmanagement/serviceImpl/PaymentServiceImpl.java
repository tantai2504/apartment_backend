package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.PaymentHistoryResponseDTO;
import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.Payment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.repository.PaymentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<PaymentHistoryResponseDTO> getPaymentHistory(Long userId, int month, int year) {

        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .filter(payment -> payment.getPaymentDate().getMonthValue() == month && payment.getPaymentDate().getYear() == year)
                .sorted(Comparator.comparing(Payment::getPaymentDate).reversed())
                .map(payment -> new PaymentHistoryResponseDTO(
                        payment.getPaymentId(),
                        payment.isPaymentCheck(),
                        payment.getPaymentDate(),
                        payment.getPaymentInfo(),
                        payment.getPaymentType(),
                        payment.getUser().getUserId(),
                        payment.getBill().getBillId()
                ))
                .collect(Collectors.toList());
    }
}
