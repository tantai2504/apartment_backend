package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getNotifications(Long userId);

    String createNotification(String notificationContent, String notiType, Long userId);
}
