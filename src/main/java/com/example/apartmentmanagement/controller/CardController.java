package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.NotificationDTO;
import com.example.apartmentmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send_card_notification/{userId}")
    public ResponseEntity<Object> sendCardCreatedNotification(@PathVariable Long userId) {
        NotificationDTO notificationDTO = notificationService.createAndBroadcastNotification(String.format("Tài khoản của bạn đã được duyệt thành công! Vui lòng xuống" +
                " lễ tân để nhận thẻ đỗ xe!"), "Thông báo cấp thẻ nhà xe", userId);
        System.out.println("hello");
        Map<String, Object> response = new HashMap<>();
        response.put("data", notificationDTO);
        response.put("status", HttpStatus.CREATED.value());
        return ResponseEntity.ok(response);
    }

}
