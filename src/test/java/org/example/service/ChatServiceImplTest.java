package org.example.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.ChatDTO;
import org.example.entity.Chat;
import org.example.entity.User;
import org.example.entity.enums.ChatType;
import org.example.mapper.ChatMapper;
import org.example.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChatServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ChatMapper chatMapper;

    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с EntityManager и маппером
        chatService = new ChatServiceImpl(entityManager, chatMapper);
    }

    @Test
    void createChat_success() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        ChatDTO expectedDto = ChatDTO.builder().id(1L).type(ChatType.PRIVATE).participantsIds(Set.of(1L, 2L)).build();

        // Мокаем createQuery для поиска существующих чатов
        TypedQuery<Chat> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Chat.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>()); // Пустой список = чат не найден

        when(entityManager.find(User.class, 1L)).thenReturn(user1);
        when(entityManager.find(User.class, 2L)).thenReturn(user2);
        when(chatMapper.toDTO(any(Chat.class))).thenReturn(expectedDto);

        ChatDTO result = chatService.createChat(1L, 2L);

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getType(), result.getType());
        assertEquals(expectedDto.getParticipantsIds(), result.getParticipantsIds());
        verify(entityManager).persist(any(Chat.class));
        verify(chatMapper).toDTO(any(Chat.class));
    }

    @Test
    void createGroupChat_success() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        User user3 = new User();
        user3.setId(3L);

        ChatDTO expectedDto = ChatDTO.builder().id(1L).type(ChatType.GROUP).participantsIds(Set.of(1L, 2L, 3L)).build();

        when(entityManager.find(User.class, 1L)).thenReturn(user1);
        when(entityManager.find(User.class, 2L)).thenReturn(user2);
        when(entityManager.find(User.class, 3L)).thenReturn(user3);
        when(chatMapper.toDTO(any(Chat.class))).thenReturn(expectedDto);

        ChatDTO result = chatService.createGroupChat(Set.of(1L, 2L, 3L));

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getType(), result.getType());
        assertEquals(expectedDto.getParticipantsIds(), result.getParticipantsIds());
        verify(entityManager).persist(any(Chat.class));
        verify(chatMapper).toDTO(any(Chat.class));
    }

    @Test
    void getUserChats_success() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Иван");

        Chat chat1 = new Chat();
        chat1.setId(1L);
        chat1.setType(ChatType.PRIVATE);
        chat1.getParticipants().add(user);

        Chat chat2 = new Chat();
        chat2.setId(2L);
        chat2.setType(ChatType.GROUP);
        chat2.getParticipants().add(user);

        List<Chat> chats = List.of(chat1, chat2);

        TypedQuery<Chat> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Chat.class))).thenReturn(query);
        when(query.setParameter(eq("userId"), eq(1L))).thenReturn(query);
        when(query.getResultList()).thenReturn(chats);
        when(chatMapper.toDTO(chat1)).thenReturn(ChatDTO.builder().id(1L).type(ChatType.PRIVATE).build());
        when(chatMapper.toDTO(chat2)).thenReturn(ChatDTO.builder().id(2L).type(ChatType.GROUP).build());

        List<ChatDTO> result = chatService.getUserChats(1L);

        assertEquals(2, result.size());
        verify(chatMapper).toDTO(chat1);
        verify(chatMapper).toDTO(chat2);
        verify(entityManager).createQuery(anyString(), eq(Chat.class));
        verify(query).setParameter(eq("userId"), eq(1L));
    }

    @Test
    void deleteChat_success() {
        Chat chat = new Chat();
        chat.setId(1L);
        chat.setType(ChatType.PRIVATE);

        User user = new User();
        user.setId(1L);
        chat.setParticipants(Set.of(user));

        when(entityManager.find(Chat.class, 1L)).thenReturn(chat);

        chatService.deleteChat(1L, 1L);

        verify(entityManager).find(Chat.class, 1L);
        verify(entityManager).remove(chat);
    }
}
