package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ChatDTO;
import org.example.entity.Chat;
import org.example.entity.User;
import org.example.entity.enums.ChatType;
import org.example.mapper.ChatMapper;
import org.example.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
            log.error("Нельзя создать чат с самим собой");
            throw new IllegalArgumentException("Нельзя создать чат с самим собой");
        }

        // Поиск существующего приватного чата с двумя участниками
        List<Chat> existingChats = entityManager.createQuery("""
                SELECT c FROM Chat c
                JOIN c.participants p1
                JOIN c.participants p2
                WHERE c.type = :type
                  AND p1.id = :id1
                  AND p2.id = :id2
                  AND SIZE(c.participants) = 2
                """, Chat.class)
                .setParameter("type", ChatType.PRIVATE)
                .setParameter("id1", userId1)
                .setParameter("id2", userId2)
                .getResultList();

        if (!existingChats.isEmpty()) {
            log.info("Чат между пользователями {} и {} уже существует", userId1, userId2);
            return chatMapper.toDTO(existingChats.get(0));
        }

        User user1 = entityManager.find(User.class, userId1);
        User user2 = entityManager.find(User.class, userId2);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("Один или оба пользователя не найдены");
        }

        Chat chat = Chat.builder()
                .type(ChatType.PRIVATE)
                .participants(Set.of(user1, user2))
                .build();

        entityManager.persist(chat);
        log.info("Создан новый приватный чат между {} и {}", userId1, userId2);
        return chatMapper.toDTO(chat);
    }

    @Override
    public ChatDTO createGroupChat(Set<Long> participantsIds) {
        if (participantsIds == null || participantsIds.size() < 2) {
            log.error("Для группового чата нужно минимум 2 участника");
            throw new IllegalArgumentException("Для группового чата нужно минимум 2 участника");
        }

        Set<User> participants = participantsIds.stream()
                .map(id -> entityManager.find(User.class, id))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (participants.size() < 2) {
            throw new IllegalArgumentException("Недостаточно валидных пользователей для создания группового чата");
        }

        Chat chat = Chat.builder()
                .type(ChatType.GROUP)
                .participants(participants)
                .build();

        entityManager.persist(chat);
        log.info("Создан новый групповой чат с участниками: {}", participantsIds);
        return chatMapper.toDTO(chat);
    }

    @Override
    public List<ChatDTO> getUserChats(Long userId) {
        List<Chat> chats = entityManager.createQuery("""
                SELECT c FROM Chat c
                JOIN c.participants p
                WHERE p.id = :userId
                """, Chat.class)
                .setParameter("userId", userId)
                .getResultList();

        return chats.stream()
                .map(chatMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteChat(Long chatId, Long requesterId) {
        Chat chat = entityManager.find(Chat.class, chatId);
        if (chat == null) {
            log.error("Чат не найден");
            throw new IllegalArgumentException("Чат не найден");
        }

        boolean isParticipant = chat.getParticipants().stream()
                .anyMatch(user -> user.getId().equals(requesterId));

        if (!isParticipant) {
            throw new SecurityException("Только участник может удалить чат");
        }

        entityManager.remove(chat);
        log.info("Чат {} удален пользователем {}", chatId, requesterId);
    }
}
