package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.BankAccountQRResponseDTO;
import com.example.apartmentmanagement.dto.BankAccountRequestDTO;
import com.example.apartmentmanagement.service.BankAccountService;
import com.example.apartmentmanagement.serviceImpl.BankAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-account")
public class BankAccountController {
    @Autowired
    private BankAccountService bankAccountService;

    @PostMapping("/add")
    public ResponseEntity<BankAccountQRResponseDTO> addBankAccount(
            @RequestBody BankAccountRequestDTO requestDTO) {
        // Thêm tài khoản và sinh QR
        BankAccountQRResponseDTO response = bankAccountService.addBankAccountWithQR(requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/qr_generate")
    public ResponseEntity<BankAccountQRResponseDTO> generateQRCode(@RequestBody BankAccountRequestDTO requestDTO) {
        BankAccountQRResponseDTO response = bankAccountService.generateQR(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-payment-status/{billId}")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable Long billId) {
        // Tạm thời mock luôn "PAID" để test FE
        return ResponseEntity.ok("PAID");
    }

    @PostMapping("/update-payment-status")
    public ResponseEntity<String> updatePaymentStatus(@RequestParam Long billId, @RequestParam String status) {
        System.out.println("✅ Trạng thái thanh toán: " + status + " - Bill ID: " + billId);
        return ResponseEntity.ok("Đã nhận trạng thái thanh toán cho billId: " + billId);
    }
}