package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.service.BillService;
import com.example.apartmentmanagement.service.DepositService;
import com.example.apartmentmanagement.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.util.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
  private final PayOS payOS;

  public PaymentController(PayOS payOS) {
    super();
    this.payOS = payOS;
  }

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private DepositService depositService;

  @Autowired
  private BillService billService;

  @PostMapping(path = "/payos_transfer_handler")
  public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
          throws JsonProcessingException, IllegalArgumentException {

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode response = objectMapper.createObjectNode();
    Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

    try {
      // Xác minh dữ liệu từ PayOS
      WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);

      // Sau khi xử lý thành công, trả về phản hồi OK
      response.put("error", 0);
      response.put("message", "Webhook delivered");
      response.set("data", null);

      return response;
    } catch (Exception e) {
      e.printStackTrace();
      response.put("error", -1);
      response.put("message", e.getMessage());
      response.set("data", null);
      return response;
    }
  }

  @PostMapping("/success")
  public ResponseEntity<Object> paymentSuccess(@RequestBody PaymentRequestDTO paymentRequestDTO) {
    Map<String, Object> response = new HashMap<>();
    try {
      billService.processPaymentSuccess(paymentRequestDTO);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Thanh toán thành công");
      response.put("data", paymentRequestDTO);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("message", e.getMessage());
      response.put("status", HttpStatus.BAD_REQUEST.value());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @PostMapping("/cancel")
  public ResponseEntity<Object> paymentCancel(@RequestBody PaymentRequestDTO paymentRequestDTO) {
    Map<String, Object> response = new HashMap<>();
    try {
      billService.processPaymentCancel(paymentRequestDTO);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Hủy thanh toán thành công");
      response.put("data", paymentRequestDTO);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("message", e.getMessage());
      response.put("status", HttpStatus.BAD_REQUEST.value());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @PostMapping("/deposit_success")
  public ResponseEntity<Object> depositSuccess(@RequestBody DepositPaymentDTO depositPaymentDTO) {
    Map<String, Object> response = new HashMap<>();
    try {
      DepositResponseDTO dto = depositService.processPaymentSuccess(depositPaymentDTO);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Thanh toán thành công");
      response.put("data", dto);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("message", e.getMessage());
      response.put("status", HttpStatus.BAD_REQUEST.value());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  @GetMapping("/history/{userId}")
  public ResponseEntity<Object> paymentHistory(@RequestParam int month, @RequestParam int year,
                                                   @PathVariable Long userId) {
    Map<String, Object> response = new HashMap<>();
    List<PaymentHistoryResponseDTO> paymentHistoryResponseDTOS = paymentService.getPaymentHistory(userId, month, year);
    if (!paymentHistoryResponseDTOS.isEmpty()) {
      response.put("data", paymentHistoryResponseDTOS);
      response.put("status", HttpStatus.OK.value());
      return ResponseEntity.ok(response);
    } else {
      response.put("status", HttpStatus.NOT_FOUND.value());
      response.put("message", "Không có dữ liệu");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
