package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.NotificationDTO;
import com.example.apartmentmanagement.entities.Notification;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getNotifications(Long userId);

    String createNotification(String notificationContent, String notiType, Long userId);

    NotificationDTO createAndBroadcastNotification(String notificationContent, String notiType, Long userId);

    String deleteNotification(Long notificationId);

    Notification getNotification(Long notificationId);

    NotificationDTO markAsReadAndBroadcast(Long notificationId);
}