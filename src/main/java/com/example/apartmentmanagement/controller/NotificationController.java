package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.NotificationDTO;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/view_all")
    List<NotificationDTO> showAllNotifications(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getUserId();
        return notificationService.getNotifications(userId);
    }
}
