package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.FriendshipDTO;
import org.example.dto.UserDTO;
import org.example.entity.Friendship;
import org.example.entity.User;
import org.example.entity.enums.FriendshipStatus;
import org.example.mapper.FriendshipMapper;
import org.example.mapper.UserMapper;
import org.example.service.impl.FriendshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class FriendshipServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private FriendshipMapper friendshipMapper;

    @Mock
    private UserMapper userMapper;

    private FriendshipServiceImpl friendshipService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с EntityManager и мапперами
        friendshipService = new FriendshipServiceImpl(entityManager, friendshipMapper, userMapper);
    }

    @Test
    void sendFriendRequestTest_success() {
        User user1 = new User();
        User user2 = new User();
        user2.setId(2L);
        user1.setId(1L);

        when(entityManager.find(User.class, 1L)).thenReturn(user1);
        when(entityManager.find(User.class, 2L)).thenReturn(user2);

        TypedQuery<Friendship> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Friendship.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        FriendshipDTO expectedDto = FriendshipDTO.builder().id(1L).status(FriendshipStatus.PENDING).build();

        when(friendshipMapper.toDTO(any(Friendship.class))).thenReturn(expectedDto);

        FriendshipDTO result = friendshipService.sendFriendRequest(1L, 2L);

        assertEquals(FriendshipStatus.PENDING, result.getStatus());
        assertEquals(expectedDto.getId(), result.getId());
        verify(entityManager, times(1)).persist(any(Friendship.class));
    }

    @Test
    void sendFriendRequestTest_selfRequest_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> friendshipService.sendFriendRequest(1L, 1L));
    }

    @Test
    void acceptFriendRequest_success() {
        Friendship friendship = Friendship.builder().id(1L).status(FriendshipStatus.PENDING).build();

        when(entityManager.find(Friendship.class, 1L)).thenReturn(friendship);

        friendshipService.acceptFriendRequest(1L);

        assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
        verify(entityManager).merge(friendship);
    }

    @Test
    void acceptFriendRequest_alreadyProcessed_throwsException() {
        Friendship friendship = Friendship.builder().id(1L).status(FriendshipStatus.ACCEPTED).build();

        when(entityManager.find(Friendship.class, 1L)).thenReturn(friendship);

        assertThrows(IllegalStateException.class, () -> friendshipService.acceptFriendRequest(1L));
    }

    @Test
    void getFriends_success() {
        User friend1 = User.builder().id(1L).username("Иван").build();
        UserDTO friendDto = UserDTO.builder().id(1L).username("Иван").build();

        // Мокаем два запроса: один для друзей как отправитель, другой как получатель
        TypedQuery<User> query1 = mock(TypedQuery.class);
        TypedQuery<User> query2 = mock(TypedQuery.class);
        
        when(entityManager.createQuery(anyString(), eq(User.class)))
                .thenReturn(query1)  // Первый вызов
                .thenReturn(query2); // Второй вызов
        
        when(query1.setParameter(anyString(), any())).thenReturn(query1);
        when(query2.setParameter(anyString(), any())).thenReturn(query2);
        
        // Первый запрос возвращает одного друга, второй - пустой список
        when(query1.getResultList()).thenReturn(List.of(friend1));
        when(query2.getResultList()).thenReturn(List.of());

        when(userMapper.toDTO(friend1)).thenReturn(friendDto);

        List<UserDTO> result = friendshipService.getFriends(1L);

        assertEquals(1, result.size());
        assertEquals("Иван", result.get(0).getUsername());
    }

    @Test
    void declineFriendRequest_success() {
        Friendship friendship = Friendship.builder().id(1L).status(FriendshipStatus.PENDING).build();

        when(entityManager.find(Friendship.class, 1L)).thenReturn(friendship);

        friendshipService.declineFriendRequest(1L);

        assertEquals(FriendshipStatus.DECLINED, friendship.getStatus());
        verify(entityManager).merge(friendship);
    }

    @Test
    void declineFriendRequest_alreadyProcessed_throwsException() {
        Friendship friendship = Friendship.builder().id(1L).status(FriendshipStatus.DECLINED).build();

        when(entityManager.find(Friendship.class, 1L)).thenReturn(friendship);

        assertThrows(IllegalArgumentException.class, () -> friendshipService.declineFriendRequest(1L));
    }

    @Test
    void removeFriend_success() {
        Friendship friendship = Friendship.builder().id(1L).status(FriendshipStatus.ACCEPTED).build();

        TypedQuery<Friendship> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Friendship.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(friendship));

        friendshipService.removeFriend(1L, 2L);

        verify(entityManager, times(1)).remove(friendship);
    }

    @Test
    void removeFriend_notFound() {
        TypedQuery<Friendship> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Friendship.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> friendshipService.removeFriend(1L, 2L));

        verify(entityManager, never()).remove(any());
    }

    @Test
    void getPendingRequests_success() {
        Friendship friendship = Friendship.builder().id(1L).status(FriendshipStatus.PENDING).build();

        TypedQuery<Friendship> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Friendship.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(friendship));

        FriendshipDTO dto = FriendshipDTO.builder().id(1L).build();
        when(friendshipMapper.toDTO(friendship)).thenReturn(dto);

        List<FriendshipDTO> result = friendshipService.getPendingRequests(2L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(friendshipMapper, times(1)).toDTO(friendship);
    }
}
