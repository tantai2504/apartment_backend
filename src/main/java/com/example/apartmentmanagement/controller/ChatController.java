package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ChatMessageDTO;
import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    // Xử lý gửi tin nhắn qua WebSocket
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO, HttpSession session) {
        User sender = (User) session.getAttribute("user");
        if (sender == null) return;

        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO, sender);

        // Chuyển đổi entity thành DTO để gửi đi
        ChatMessageDTO messageDTO = convertToDTO(savedMessage);

        // Gửi tin nhắn đến người nhận
        messagingTemplate.convertAndSendToUser(
                String.valueOf(savedMessage.getReceiver().getUserId()),
                "/queue/messages",
                messageDTO
        );

        // Gửi xác nhận về cho người gửi
        messagingTemplate.convertAndSendToUser(
                String.valueOf(sender.getUserId()),
                "/queue/messages",
                messageDTO
        );
    }

    // API lấy lịch sử tin nhắn giữa 2 người
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long userId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        try {
            User otherUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            List<ChatMessage> messages = chatService.getMessagesBetweenUsers(currentUser, otherUser);

            // Đánh dấu tin nhắn là đã đọc
            chatService.markMessagesAsRead(otherUser, currentUser);

            List<ChatMessageDTO> messageDTOs = messages.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(messageDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API lấy danh sách người dùng đã chat với người dùng hiện tại
    @GetMapping("/contacts")
    public ResponseEntity<?> getChatContacts(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        try {
            List<User> contacts = chatService.getChatContacts(currentUser);

            // Thêm thông tin số tin nhắn chưa đọc
            List<Map<String, Object>> contactsWithUnreadCount = contacts.stream()
                    .map(user -> {
                        Map<String, Object> contactInfo = new HashMap<>();
                        contactInfo.put("userId", user.getUserId());
                        contactInfo.put("userName", user.getUserName());
                        contactInfo.put("fullName", user.getFullName());
                        contactInfo.put("userImgUrl", user.getUserImgUrl());
                        contactInfo.put("unreadCount", chatService.countUnreadMessages(user, currentUser));
                        return contactInfo;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(contactsWithUnreadCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API đánh dấu tin nhắn là đã đọc
    @PostMapping("/read/{userId}")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long userId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập");
        }

        try {
            User otherUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            chatService.markMessagesAsRead(otherUser, currentUser);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper method để chuyển đổi Entity sang DTO
    private ChatMessageDTO convertToDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getUserId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setReceiverId(message.getReceiver().getUserId());
        dto.setReceiverName(message.getReceiver().getFullName());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setRead(message.isRead());
        return dto;
    }
}