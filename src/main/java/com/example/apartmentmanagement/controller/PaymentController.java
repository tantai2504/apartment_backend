package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.service.BillService;
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

@RestController
@RequestMapping("/payment")
public class PaymentController {
  private final PayOS payOS;

  public PaymentController(PayOS payOS) {
    super();
    this.payOS = payOS;

  }

  @Autowired
  private BillService billService;

  @PostMapping(path = "/payos_transfer_handler")
  public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
      throws JsonProcessingException, IllegalArgumentException {

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode response = objectMapper.createObjectNode();
    Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

    try {
      // Init Response
      response.put("error", 0);
      response.put("message", "Webhook delivered");
      response.set("data", null);

      WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
      System.out.println(data);
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
  public ResponseEntity<Object> paymentSuccess(@RequestParam Long billId, @RequestParam String paymentInfo) {
    try {
      System.out.println("billId: " + billId);
      System.out.println("paymentInfo: " + paymentInfo);
      billService.processPaymentSuccess(billId, paymentInfo);
      return ResponseEntity.ok("Thanh toán thành công");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

}
