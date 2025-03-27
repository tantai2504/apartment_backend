package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.NotificationDTO;
import com.example.apartmentmanagement.entities.Notification;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.NotificationRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public List<NotificationDTO> getNotifications(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user.getNotifications().stream().map(notification -> new NotificationDTO(
                notification.getNotificationId(),
                user.getUserName(),
                notification.getNotificationContent(),
                notification.isNotificationCheck(),
                notification.getNotificationDate(),
                notification.getNotificationType()
        )).collect(Collectors.toList());
    }

    @Override
    public String createNotification(String notificationContent, String notiType, Long userId) {
        Notification newNotification = new Notification();
        newNotification.setNotificationContent(notificationContent);
        User user = userRepository.findById(userId).get();
        newNotification.setNotificationType(notiType);
        newNotification.setNotificationDate(LocalDateTime.now());
        newNotification.setNotificationCheck(false);
        newNotification.setUser(user);
        notificationRepository.save(newNotification);
        return "done";
    }

    @Override
    public NotificationDTO createAndBroadcastNotification(String notificationContent, String notiType, Long userId) {
        Notification newNotification = new Notification();
        newNotification.setNotificationContent(notificationContent);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        newNotification.setNotificationType(notiType);
        newNotification.setNotificationDate(LocalDateTime.now());
        newNotification.setNotificationCheck(false);
        newNotification.setUser(user);

        notificationRepository.save(newNotification);

        // Create DTO for WebSocket
        NotificationDTO dto = new NotificationDTO(
                newNotification.getNotificationId(),
                user.getUserName(),
                newNotification.getNotificationContent(),
                newNotification.isNotificationCheck(),
                newNotification.getNotificationDate(),
                newNotification.getNotificationType()
        );

        // Send to specific user
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notifications",
                dto
        );

        return dto;
    }

    @Override
    public String deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        return "done";
    }

    @Override
    public Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElse(null);
    }

    /**
     * Marks a notification as read and broadcasts the update
     * @param notificationId The ID of the notification to mark as read
     * @return The updated notification as DTO
     */
    @Override
    public NotificationDTO markAsReadAndBroadcast(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        notification.setNotificationCheck(true);
        notificationRepository.save(notification);

        // Tạo DTO để gửi qua WebSocket
        NotificationDTO dto = new NotificationDTO(
                notification.getNotificationId(),
                notification.getUser().getUserName(),
                notification.getNotificationContent(),
                notification.isNotificationCheck(),
                notification.getNotificationDate(),
                notification.getNotificationType()
        );

        // Gửi đến người dùng cụ thể
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getUser().getUserId()),
                "/queue/notifications",
                dto
        );

        return dto;
    }
}