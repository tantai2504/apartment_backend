package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String sender;
    private String content;
    private String chatId;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}