package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
