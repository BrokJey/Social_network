package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ChatDTO;
import org.example.entity.Chat;
import org.example.entity.enums.ChatType;
import org.example.mapper.ChatMapper;
import org.example.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ChatMapper chatMapper;

    @Override
    public ChatDTO createChat(Long userId1, Long userId2) {
        if (Objects.equals(userId1, userId2)) {
            log.error("Нельзя создать чат с самим собой (id1 = {}, id2 - {})", userId1, userId2);
            throw new IllegalArgumentException("Нельзя создать чат с самим собой");
        }

        List<Chat> existingChats = entityManager.createQuery("" +
                "SELECT c FROM Chat c" +
                "JOIN c.participants p1" +
                "Join c.participants p2" +
                "WHERE c.type = :type" +
                "AND p1.id = :id1" +
                "AND p1.id = :id2", Chat.class)
                .setParameter("type", ChatType.PRIVATE)
                .setParameter("id1", userId1)
                .setParameter("id2", userId2)
                .getResultList();

        if (!existingChats.isEmpty()) {
            log.info("Чат между {} и {} уже существует", userId1, userId2);
            return chatMapper.toDTO(existingChats.get(0));
        }
    }
}
