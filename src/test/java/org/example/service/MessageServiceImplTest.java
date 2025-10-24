package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.MessageDTO;
import org.example.entity.Chat;
import org.example.entity.Message;
import org.example.entity.User;
import org.example.mapper.MessageMapper;
import org.example.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MessageServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private MessageMapper messageMapper;

    private MessageServiceImpl messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с EntityManager и маппером
        messageService = new MessageServiceImpl(entityManager, messageMapper);
    }

    @Test
    void sendMessage_success() {
        User user1 = new User();
        user1.setId(1L);
        MessageDTO inputDto = MessageDTO.builder().content("Привет!").senderId(1L).build();
        Message messageEntity = new Message();
        messageEntity.setSender(user1);
        messageEntity.setContent("Привет!");
        messageEntity.setId(1L);
        Chat chat = new Chat();
        chat.setId(1L);

        MessageDTO expectedDto = MessageDTO.builder().id(1L).content("Привет!").senderId(1L).build();

        when(messageMapper.fromDTO(inputDto)).thenReturn(messageEntity);
        doAnswer(invocation -> {
            Message m = invocation.getArgument(0);
            m.setId(1L);
            return null;
        }).when(entityManager).persist(messageEntity);

        when(messageMapper.toDTO(any(Message.class))).thenReturn(expectedDto);
        when(entityManager.find(Chat.class, 1L)).thenReturn(chat);
        when(entityManager.find(User.class, 1L)).thenReturn(user1);


        MessageDTO result = messageService.sendMessage(1L, 1L, inputDto);

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getContent(), result.getContent());
        verify(entityManager).persist(messageEntity);
        verify(messageMapper).toDTO(messageEntity);
    }

    @Test
    void sendMessage_null_throwsException() {
        User user1 = new User();
        user1.setId(1L);
        MessageDTO inputDto = MessageDTO.builder().content(null).build();
        Message messageEntity = new Message();
        messageEntity.setSender(user1);
        messageEntity.setContent("Привет!");
        messageEntity.setId(1L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> messageService.sendMessage(1L, 1L, inputDto));
        assertEquals("Error: содержимое сообщение не может быть пустым", ex.getMessage());
        verifyNoInteractions(entityManager, messageMapper);
    }

    @Test
    void getMessageById_found() {
        Message message = new Message();
        message.setId(1L);
        message.setContent("Привет");

        MessageDTO dto = MessageDTO.builder().id(1L).content("Привет!").build();

        when(entityManager.find(Message.class, 1L)).thenReturn(message);
        when(messageMapper.toDTO(message)).thenReturn(dto);

        MessageDTO result = messageService.getMessageById(1L).orElse(null);

        assertEquals(dto.getId(), result.getId());
        verify(entityManager).find(Message.class, 1L);
        verify(messageMapper).toDTO(message);
    }

    @Test
    void getMessagesBetweenUsers_success() {
        Chat chat = new Chat();
        chat.setId(1L);

        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("Первое сообщение");
        Message message2 = new Message();
        message2.setId(2L);
        message2.setContent("Второе сообщение");

        TypedQuery<Chat> chatQuery = mock(TypedQuery.class);
        TypedQuery<Message> messagesQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(startsWith("SELECT c FROM Chat"), eq(Chat.class))).thenReturn(chatQuery);
        when(chatQuery.setParameter(eq("userId1"), eq(1L))).thenReturn(chatQuery);
        when(chatQuery.setParameter(eq("userId2"), eq(2L))).thenReturn(chatQuery);
        when(chatQuery.setParameter(eq("chatType"), any())).thenReturn(chatQuery);
        when(chatQuery.getResultList()).thenReturn(List.of(chat));

        when(entityManager.createQuery(startsWith("SELECT m FROM Message"), eq(Message.class))).thenReturn(messagesQuery);
        when(messagesQuery.setParameter(eq("chatId"), eq(1L))).thenReturn(messagesQuery);
        when(messagesQuery.getResultList()).thenReturn(List.of(message1, message2));

        when(messageMapper.toDTO(message1)).thenReturn(MessageDTO.builder().id(1L).content("Первое сообщение").build());
        when(messageMapper.toDTO(message2)).thenReturn(MessageDTO.builder().id(2L).content("Второе сообщение").build());

        List<MessageDTO> result = messageService.getMessageBetweenUsers(1L, 2L);

        assertEquals(2, result.size());
        assertEquals("Первое сообщение", result.get(0).getContent());
        assertEquals("Второе сообщение", result.get(1).getContent());
    }


    @Test
    void getMessagesForUser_success() {
        List<Long> chatIds = List.of(1L);

        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("Первое сообщение");

        Message message2 = new Message();
        message2.setId(2L);
        message2.setContent("Второе сообщение");

        var chatIdsQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(startsWith("SELECT c.id"), eq(Long.class))).thenReturn(chatIdsQuery);
        when(chatIdsQuery.setParameter(eq("userId"), eq(1L))).thenReturn(chatIdsQuery);
        when(chatIdsQuery.getResultList()).thenReturn(chatIds);

        var messagesQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(startsWith("SELECT m FROM Message"), eq(Message.class))).thenReturn(messagesQuery);
        when(messagesQuery.setParameter(eq("chatIds"), eq(chatIds))).thenReturn(messagesQuery);
        when(messagesQuery.getResultList()).thenReturn(List.of(message1, message2));

        when(messageMapper.toDTO(message1)).thenReturn(MessageDTO.builder().id(1L).content("Первое сообщение").build());
        when(messageMapper.toDTO(message2)).thenReturn(MessageDTO.builder().id(2L).content("Второе сообщение").build());

        List<MessageDTO> result = messageService.getAllMessagesForUser(1L);

        assertEquals(2, result.size());
        assertEquals("Первое сообщение", result.get(0).getContent());
        assertEquals("Второе сообщение", result.get(1).getContent());
    }

    @Test
    void deleteMessage_success() {
        Message message = new Message();
        message.setId(1L);
        message.setContent("Сообщение");

        when(entityManager.find(Message.class, 1L)).thenReturn(message);

        messageService.deleteMessage(1L);

        verify(entityManager).find(Message.class, 1L);
        verify(entityManager).remove(message);
    }
}
