package org.example.service;

import org.example.dto.MessageDTO;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    MessageDTO sendMessage(Long chatId, Long senderId, MessageDTO messageDTO);
    Optional<MessageDTO> getMessageById(Long messageId);
    List<MessageDTO> getMessageBetweenUsers(Long userId1, Long userId2);
    List<MessageDTO> getAllMessagesForUser(Long userId);
    void deleteMessage(Long messageId);
}
