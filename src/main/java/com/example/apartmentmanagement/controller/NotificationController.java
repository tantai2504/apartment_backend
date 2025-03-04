package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.NotificationDTO;
import com.example.apartmentmanagement.entities.Notification;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/view_all")
    public List<NotificationDTO> showAllNotifications(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getUserId();
        return notificationService.getNotifications(userId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteNotification(Long notificationId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getUserId();
        Notification notification = notificationService.getNotification(notificationId);
        if (notification == null || !notification.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

}
