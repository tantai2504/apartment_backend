package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDTO {
    private Long id;
    private String userName;
    private String notificationContent;
    private boolean status;
    private LocalDateTime date;
    private String notificationType;

    public NotificationDTO() {
    }

    public NotificationDTO(Long id, String userName, String notificationContent, boolean status, LocalDateTime date, String notificationType) {
        this.id = id;
        this.userName = userName;
        this.notificationContent = notificationContent;
        this.status = status;
        this.date = date;
        this.notificationType = notificationType;
    }
}
