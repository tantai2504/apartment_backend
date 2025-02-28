package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.Payment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.repository.PaymentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Override
    public Payment processPayment(Long billId, String username) {
        Bill bill = billRepository.findById(billId).orElse(null);

        // Kiểm tra hóa đơn đã thanh toán chưa
        if (bill.getPayment() != null) {
            throw new RuntimeException("Hóa đơn đã được thanh toán");
        }

        // Lấy thông tin user từ username
        User user = userRepository.findByUserName(username);


        Payment payment = new Payment();
        payment.setPaymentCheck(true);
        payment.setPaymentInfo("Thanh toán hóa đơn " + billId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUser(user);

        payment = paymentRepository.save(payment);
        bill.setPayment(payment);
        bill.setStatus("payed");
        billRepository.save(bill);

        return payment;
    }
}
