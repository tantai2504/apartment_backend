package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    //lay all tin nhan
    @Query("SELECT m FROM ChatMessage m WHERE (m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1) ORDER BY m.timestamp ASC")
    List<ChatMessage> findMessagesBetweenUsers(User user1, User user2);

    // lay tin nhan chua doc
    List<ChatMessage> findByReceiverAndIsReadFalse(User receiver);

    // Tim user
    @Query("SELECT DISTINCT m.sender FROM ChatMessage m WHERE m.receiver = ?1 " +
            "UNION " +
            "SELECT DISTINCT m.receiver FROM ChatMessage m WHERE m.sender = ?1")
    List<User> findUsersChatWith(User user);
}