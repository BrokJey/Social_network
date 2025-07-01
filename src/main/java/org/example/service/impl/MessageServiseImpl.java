package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.MessageDTO;
import org.example.entity.Chat;
import org.example.entity.Message;
import org.example.entity.User;
import org.example.mapper.MessageMapper;
import org.example.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiseImpl implements MessageService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final MessageMapper messageMapper;

    @Override
    public MessageDTO sendMessage(Long chatId, Long senderId, MessageDTO messageDTO) {
        if (messageDTO.getContent() == null || messageDTO.getContent().trim().isEmpty()) {
            log.error("Error: содержимое сообщения не может быть пустым");
            throw new IllegalArgumentException("Содержимое сообщение не может быть пустым");
        }

        Chat chat = entityManager.find(Chat.class, chatId);
        User sender = entityManager.find(User.class, senderId);

        if (sender == null || chat == null) {
            log.error("Error: отправитель или чат не найдены (senderId = {}, chatId = {}", senderId, chatId);
            throw new IllegalArgumentException("Отправитель или чат не найдены");
        }

        Message message = messageMapper.fromDTO(messageDTO);
        message.setChat(chat);
        message.setSender(sender);
        message.setSentAt(LocalDateTime.now());

        entityManager.persist(message);
        log.info("Info: сообщение отправлено в чат {} от пользователя {} с id (message = {})", chatId, senderId, message.getId());
        return messageMapper.toDTO(message);
    }

    @Override
    public Optional<MessageDTO> getMessageById(Long messageId) {
        Message message = entityManager.find(Message.class, messageId);
        if (message != null) {
            log.info("Info: сообщение найдено с id {}", message.getId());
            return Optional.of(messageMapper.toDTO(message));
        } else {
            log.error("Error: сообщение не найдено с id {}", messageId);
            return Optional.empty();
        }
    }

    @Override
    public List<MessageDTO> getMessageBetweenUsers(Long userId1, Long userId2) {
        List<Chat> chats = entityManager.createQuery(
                "SELECT c FROM Chat c JOIN c.participants p1 JOIN c.participants p2 WHERE p1.id = :userId1 AND p2.id = :userId2 AND c.isGroup = false", Chat.class)
                .setParameter("userId1", userId1)
                .setParameter("userId2", userId2)
                .getResultList();

        if (chats.isEmpty()) {
            log.error("Error: чат между пользователями {} и {} не найден", userId1, userId2);
            return List.of();
        }

        Chat chat = chats.get(0);

        List<Message> messages = entityManager.createQuery(
                "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt ASC", Message.class)
                .setParameter("chatId", chat.getId())
                .getResultList();

        log.info("Info: найдено {} сообщений между пользователями {} и {}", messages.size(), userId1, userId2);

        return messages.stream()
                .map(messageMapper::toDTO)
                .toList();
    }

    @Override
    public List<MessageDTO> getAllMessagesForUser(Long userId) {
        List<Long> chatIds = entityManager.createQuery("SELECT c.id FROM Chat c JOIN c.participants p WHERE p.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getResultList();

        if (chatIds.isEmpty()) {
            log.error("Error: у пользователя {} нет чатов", userId);
            return List.of();
        }

        List<Message> messages = entityManager.createQuery("SELECT m FROM Message m WHERE m.chat.id IN :chatIds ORDER BY m.sentAt ASC", Message.class)
                .setParameter("chatIds", chatIds)
                .getResultList();

        log.info("Info: найдено {} сообщения пользователя {}", messages.size(), userId);

        return messages.stream()
                .map(messageMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteMessage (Long messageId) {
        Message message = entityManager.find(Message.class, messageId);
        if (message != null) {
            log.info("Info: сообщение {} удалено", messageId);
            entityManager.remove(message);
        }
        else {
            log.error("Error: сообщение с id {} не найдено и не может быть удалено", messageId);
        }
    }
}
