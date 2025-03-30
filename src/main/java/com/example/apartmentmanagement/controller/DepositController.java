package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.DepositRequestDTO;
import com.example.apartmentmanagement.dto.DepositResponseDTO;
import com.example.apartmentmanagement.repository.PostRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.DepositService;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.*;

@RestController
@RequestMapping("/deposit")
public class DepositController {

    @Autowired
    private PayOS payOS;

    @Autowired
    private DepositService depositService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

//    @PostMapping("/create")
//    public ResponseEntity<Object> makeDeposit(@RequestBody DepositRequestDTO depositRequestDTO) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            DepositResponseDTO dto = depositService.depositFlag(depositRequestDTO);
//            response.put("status", HttpStatus.CREATED.value());
//            response.put("message", "Tiến hành chuyển tiền");
//            response.put("data", dto);
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            response.put("message", e.getMessage());
//            response.put("status", HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> makeDeposit(
            @RequestBody DepositRequestDTO depositRequestDTO) {
        try {
            System.out.println(depositRequestDTO);
            DepositResponseDTO dto = depositService.depositFlag(depositRequestDTO);
            // Tạo thông tin thanh toán
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            // Tạo item cho PayOS
            ItemData item = ItemData.builder()
                    .name("Đặt cọc căn hộ")
                    .price((int) depositRequestDTO.getDepositPrice())
                    .quantity(1)
                    .build();

            // Tạo payment data
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount((int) depositRequestDTO.getDepositPrice())
                    .description("Đặt cọc căn hộ")
                    .item(item)
                    .returnUrl(depositRequestDTO.getSuccessUrl())
                    .cancelUrl(depositRequestDTO.getCancelUrl())
                    .build();

            // Tạo link thanh toán
            CheckoutResponseData checkoutData = payOS.createPaymentLink(paymentData);

            // Chuẩn bị response
            Map<String, Object> response = new HashMap<>();
            response.put("error", 0);
            response.put("message", "Bắt đầu quá trình đặt cọc");
            response.put("data", createPaymentResponseData(checkoutData));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", -1);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private Map<String, Object> createPaymentResponseData(CheckoutResponseData checkoutData) {
        Map<String, Object> data = new HashMap<>();
        data.put("bin", "970422");
        data.put("accountNumber", "VQRQABVKS5356");
        data.put("accountName", "HUYNH LE PHUONG NAM");
        data.put("amount", checkoutData.getAmount());
        data.put("description", "Hoa don can ho");
        data.put("orderCode", checkoutData.getOrderCode());
        data.put("currency", "VND");
        data.put("paymentLinkId", checkoutData.getPaymentLinkId());
        data.put("status", "PENDING");
        data.put("checkoutUrl", checkoutData.getCheckoutUrl());
        data.put("qrCode", checkoutData.getQrCode());
        return data;
    }

    @PostMapping("/cancel")
    public ResponseEntity<Object> cancelDeposit(@RequestBody DepositRequestDTO depositRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            DepositResponseDTO dto = depositService.cancel(depositRequestDTO);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Huỷ cọc");
            response.put("data", dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
