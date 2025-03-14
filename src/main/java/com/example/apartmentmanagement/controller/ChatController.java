package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ChatMessageDTO;
import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO);
        messagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getChatId(), savedMessage);
    }

    @MessageMapping("/chat.privateMessage")
    public void privateMessage(@Payload ChatMessageDTO chatMessageDTO) {
        ChatMessage savedMessage = chatService.saveMessage(chatMessageDTO);
        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getChatId(),
                "/messages",
                savedMessage
        );
    }

    @GetMapping("/chat/history/{chatId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String chatId) {
        return ResponseEntity.ok(chatService.getMessagesByChatId(chatId));
    }

    @GetMapping("/chat/room")
    @ResponseBody
    public ResponseEntity<String> getChatRoomId(
            @RequestParam String sender,
            @RequestParam String receiver
    ) {
        String chatId = chatService.getChatRoomId(sender, receiver);
        return ResponseEntity.ok(chatId);
    }
}