package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Lấy tất cả tin nhắn giữa hai người dùng
    @Query("SELECT m FROM ChatMessage m WHERE (m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1) ORDER BY m.timestamp ASC")
    List<ChatMessage> findMessagesBetweenUsers(User user1, User user2);

    // Lấy tin nhắn chưa đọc
    List<ChatMessage> findByReceiverAndIsReadFalse(User receiver);

    // Tìm danh sách người dùng đã chat với một người dùng
    @Query("SELECT DISTINCT m.sender FROM ChatMessage m WHERE m.receiver = ?1 " +
            "UNION " +
            "SELECT DISTINCT m.receiver FROM ChatMessage m WHERE m.sender = ?1")
    List<User> findUsersChatWith(User user);

    // Đánh dấu tin nhắn là đã đọc
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.sender = :sender AND m.receiver = :receiver AND m.isRead = false")
    void markMessagesAsRead(@Param("sender") User sender, @Param("receiver") User receiver);

    // Đếm số tin nhắn chưa đọc từ một người dùng
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.sender = :sender AND m.receiver = :receiver AND m.isRead = false")
    int countUnreadMessages(@Param("sender") User sender, @Param("receiver") User receiver);
}