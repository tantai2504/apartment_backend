package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.NotificationDTO;
import com.example.apartmentmanagement.entities.Notification;
import com.example.apartmentmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Lấy tất cả thông báo của một người dùng
     */
    @GetMapping("/view_all")
    public ResponseEntity<Object> showAllNotifications(@RequestParam Long userId) {
        List<NotificationDTO> dtos = notificationService.getNotifications(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("data", dtos);
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa một thông báo
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteNotification(@RequestParam Long notificationId, @RequestParam Long userId) {
        Notification notification = notificationService.getNotification(notificationId);
        Map<String, Object> response = new HashMap<>();

        if (notification == null || !notification.getUser().getUserId().equals(userId)) {
            response.put("status", HttpStatus.FORBIDDEN.value());
            response.put("message", "Không có quyền xóa thông báo này");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        notificationService.deleteNotification(notificationId);
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Đã xóa thông báo thành công");
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo và gửi thông báo cho một người dùng
     */
    @PostMapping("/send")
    public ResponseEntity<Object> sendNotification(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        try {
            String content = (String) payload.get("content");
            String type = (String) payload.get("type");
            Long userId = Long.parseLong(payload.get("userId").toString());

            NotificationDTO notificationDTO = notificationService.createAndBroadcastNotification(content, type, userId);

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Thông báo đã được gửi thành công");
            response.put("data", notificationDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * WebSocket endpoint để xử lý thông báo đã đọc
     */
    @MessageMapping("/notification.read")
    public void processReadNotification(@Payload Map<String, Object> payload) {
        try {
            Long notificationId = Long.parseLong(payload.get("notificationId").toString());
            Long userId = Long.parseLong(payload.get("userId").toString());

            Notification notification = notificationService.getNotification(notificationId);
            if (notification != null && notification.getUser().getUserId().equals(userId)) {
                notificationService.markAsReadAndBroadcast(notificationId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi thông báo đến tất cả người dùng (chỉ admin mới có quyền)
     */
    @PostMapping("/broadcast-all")
    public ResponseEntity<Object> broadcastToAll(@RequestBody Map<String, Object> payload, @RequestParam String role) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra quyền admin
        if (!"admin".equalsIgnoreCase(role) && !"staff".equalsIgnoreCase(role)) {
            response.put("status", HttpStatus.FORBIDDEN.value());
            response.put("message", "Không có quyền gửi thông báo toàn cục");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            String content = (String) payload.get("content");
            String type = (String) payload.get("type");

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setNotificationContent(content);
            notificationDTO.setNotificationType(type);
            notificationDTO.setDate(java.time.LocalDateTime.now());

            // Gửi đến tất cả subscribers
            messagingTemplate.convertAndSend("/topic/global-notifications", notificationDTO);

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Đã gửi thông báo toàn cục thành công");
            response.put("data", notificationDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}