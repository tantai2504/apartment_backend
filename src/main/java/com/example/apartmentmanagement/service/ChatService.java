package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ChatMessageDTO;
import com.example.apartmentmanagement.entities.ChatMessage;

import java.util.List;

public interface ChatService {
    ChatMessage saveMessage(ChatMessageDTO chatMessage);
    List<ChatMessage> getMessagesByChatId(String chatId);
    String getChatRoomId(String sender, String receiver);
}

