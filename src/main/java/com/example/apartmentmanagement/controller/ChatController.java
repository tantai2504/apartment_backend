package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ChatMessageDTO;
import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ChatService;
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

    @GetMapping("/chat-page")
    public ResponseEntity<?> getChatPage(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("Lỗi: Không tìm thấy người dùng.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("userName", user.getUserName());

        return ResponseEntity.ok(response);
    }

    // Xử lý gửi tin nhắn qua WebSocket
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        // Sử dụng senderId từ chatMessageDTO thay vì từ HttpSession
        Long senderId = chatMessageDTO.getSenderId();
        User sender = userRepository.findById(senderId).orElse(null);

        if (sender == null) return;

        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO, sender);

        ChatMessageDTO messageDTO = convertToDTO(savedMessage);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(savedMessage.getReceiver().getUserId()),
                "/queue/messages",
                messageDTO
        );
        messagingTemplate.convertAndSendToUser(
                String.valueOf(sender.getUserId()),
                "/queue/messages",
                messageDTO
        );
    }


    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long userId, @RequestParam Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
        }

        try {
            User otherUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            List<ChatMessage> messages = chatService.getMessagesBetweenUsers(currentUser, otherUser);


            chatService.markMessagesAsRead(otherUser, currentUser);

            List<ChatMessageDTO> messageDTOs = messages.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(messageDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getChatContacts(@RequestParam Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
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
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long userId, @RequestParam Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng");
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

    // Helper method để chuyển đổi Entity sang DTO - Đã sửa để chuyển timestamp sang String
    private ChatMessageDTO convertToDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getUserId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setReceiverId(message.getReceiver().getUserId());
        dto.setReceiverName(message.getReceiver().getFullName());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp().toString()); // Chuyển đổi sang String
        dto.setRead(message.isRead());
        return dto;
    }
}