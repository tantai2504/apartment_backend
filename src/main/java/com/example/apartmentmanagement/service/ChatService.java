package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ChatMessageDTO;
import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.entities.User;

import java.util.List;

public interface ChatService {
    // Lưu tin nhắn mới
    ChatMessage saveMessage(ChatMessageDTO messageDTO, User sender);

    // Lấy tất cả tin nhắn giữa 2 người dùng
    List<ChatMessage> getMessagesBetweenUsers(User user1, User user2);

    // Đánh dấu tất cả tin nhắn từ một người dùng là đã đọc
    void markMessagesAsRead(User sender, User receiver);

    // Lấy danh sách người dùng đã chat với người dùng hiện tại
    List<User> getChatContacts(User user);

    // Đếm số tin nhắn chưa đọc từ một người dùng
    int countUnreadMessages(User sender, User receiver);
}