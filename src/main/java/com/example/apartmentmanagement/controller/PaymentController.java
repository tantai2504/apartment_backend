package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.entities.Payment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestParam Long billId, HttpSession session) {
        Object sessionUser = session.getAttribute("user");
        if (sessionUser == null) {
            throw new RuntimeException("User not found in session");
        }
        User user = (User) sessionUser;
        try {
            Payment payment = paymentService.processPayment(billId, user.getUserName());
            return ResponseEntity.ok("Thanh toán thành công! ID Payment: " + payment.getPaymentId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
