package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ChatMessageDTO;
import com.example.apartmentmanagement.entities.ChatMessage;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ChatMessageRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ChatMessage saveMessage(ChatMessageDTO messageDTO, User sender) {
        User receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getMessagesBetweenUsers(User user1, User user2) {
        return chatMessageRepository.findMessagesBetweenUsers(user1, user2);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(User sender, User receiver) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findAll().stream()
                .filter(msg -> msg.getSender().equals(sender) &&
                        msg.getReceiver().equals(receiver) &&
                        !msg.isRead())
                .collect(Collectors.toList());

        unreadMessages.forEach(msg -> msg.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }

    @Override
    public List<User> getChatContacts(User user) {
        return chatMessageRepository.findUsersChatWith(user);
    }

    @Override
    public int countUnreadMessages(User sender, User receiver) {
        return (int) chatMessageRepository.findAll().stream()
                .filter(msg -> msg.getSender().equals(sender) &&
                        msg.getReceiver().equals(receiver) &&
                        !msg.isRead())
                .count();
    }
}