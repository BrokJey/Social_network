package org.example.service;

import org.example.dto.ChatDTO;

import java.util.List;
import java.util.Set;

public interface ChatService {
    ChatDTO createChat(Long userId1, Long userId2);
    ChatDTO createGroupChat(Set<Long> participantsIds);
    List<ChatDTO> getUserChats(Long userId);
    void deleteChat(Long chatId, Long requesterId);
}