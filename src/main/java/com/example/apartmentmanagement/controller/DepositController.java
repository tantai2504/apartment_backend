package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.DepositListResponseDTO;
import com.example.apartmentmanagement.dto.DepositPaymentDTO;
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

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllDeposits() {
        List<DepositListResponseDTO> deposits = depositService.getAllDeposits();
        Map<String, Object> response = new HashMap<>();
        if (deposits.isEmpty()) {
            response.put("message", "Không có đợt đặt cọc nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        }
        response.put("status", HttpStatus.OK.value());
        response.put("data", deposits);
        return ResponseEntity.ok(response);
    }

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

            Map<String, Object> paymentResponseData = depositService.paymentResponseData(checkoutData, dto);

            // Chuẩn bị response
            Map<String, Object> response = new HashMap<>();
            response.put("error", 0);
            response.put("message", "Bắt đầu quá trình đặt cọc");
            response.put("data", paymentResponseData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", -1);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<Object> cancelDeposit(@RequestBody DepositPaymentDTO depositPaymentDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            DepositResponseDTO dto = depositService.cancel(depositPaymentDTO);
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
