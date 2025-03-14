package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ChatMessageDTO;
import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.entities.ChatRoom;
import com.example.apartmentmanagement.repository.ChatMessageRepository;
import com.example.apartmentmanagement.repository.ChatRoomRepository;
import com.example.apartmentmanagement.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatMessageRepository messageRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Override
    public ChatMessage saveMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage message = new ChatMessage();
        message.setSender(chatMessageDTO.getSender());
        message.setContent(chatMessageDTO.getContent());
        message.setChatId(chatMessageDTO.getChatId());
        return messageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getMessagesByChatId(String chatId) {
        return messageRepository.findByChatId(chatId);
    }

    @Override
    public String getChatRoomId(String sender, String receiver) {
        // Kiểm tra cả hai trường hợp để đảm bảo tìm được phòng chat
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByParticipant1AndParticipant2(sender, receiver);

        if (!chatRoom.isPresent()) {
            chatRoom = chatRoomRepository.findByParticipant1AndParticipant2(receiver, sender);
        }

        return chatRoom.map(ChatRoom::getChatId).orElseGet(() -> {
            String chatId = UUID.randomUUID().toString();
            chatRoomRepository.save(new ChatRoom(null, chatId, sender, receiver));
            return chatId;
        });
    }
}