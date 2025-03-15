package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByParticipant1AndParticipant2(String user1, String user2);
    Optional<ChatRoom> findByParticipant2AndParticipant1(String user1, String user2);
    Optional<ChatRoom> findByChatId(String chatId);
}