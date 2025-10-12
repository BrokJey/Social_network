package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class UserServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с EntityManager и маппером
        userService = new UserServiceImpl(entityManager, userMapper);
    }

    @Test
    void registerUser_success() {
        UserDTO inputDto = UserDTO.builder().firstName("Иван").build();
        User userEntity = new User();
        userEntity.setFirstName("Иван");
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Иван");

        UserDTO expectedDto = UserDTO.builder().id(1L).firstName("Иван").build();

        when(userMapper.fromDTO(inputDto)).thenReturn(userEntity);
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return null;
        }).when(entityManager).persist(userEntity);

        when(userMapper.toDTO(any(User.class))).thenReturn(expectedDto);

        UserDTO result = userService.registerUser(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getFirstName(), result.getFirstName());
        verify(entityManager).persist(userEntity);
        verify(userMapper).toDTO(userEntity);
    }

    @Test
    void registerUser_nullFirstName_throwsException() {
        UserDTO inputDto = UserDTO.builder().firstName(null).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(inputDto));
        assertEquals("Error: имя пользователя не может быть null", ex.getMessage());
        verifyNoInteractions(entityManager, userMapper);
    }

    @Test
    void getUserById_found() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Иван");

        UserDTO dto = UserDTO.builder().id(1L).firstName("Иван").build();

        when(entityManager.find(User.class, 1L)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(dto);

        UserDTO result = userService.getUserById(1L);

        assertEquals(dto.getId(), result.getId());
        verify(entityManager).find(User.class, 1L);
        verify(userMapper).toDTO(user);
    }

    @Test
    void getUserById_notFound_throwsException() {
        when(entityManager.find(User.class, 1L)).thenReturn(null);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        assertEquals("Пользователь не найден", ex.getMessage());
        verify(entityManager).find(User.class, 1L);
    }

    @Test
    void updateUser_success() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("Old");
        existingUser.setLastName("Name");
        existingUser.setAge(20);

        UserDTO updateDto = UserDTO.builder()
                .firstName("New")
                .lastName("User")
                .age(30)
                .build();

        when(entityManager.find(User.class, 1L)).thenReturn(existingUser);

        UserDTO expectedDto = UserDTO.builder()
                .id(1L)
                .firstName("New")
                .lastName("User")
                .age(30)
                .build();

        when(userMapper.toDTO(existingUser)).thenReturn(expectedDto);

        UserDTO updated = userService.updateUser(1L, updateDto);

        assertEquals(expectedDto, updated);
        verify(entityManager).merge(existingUser);
    }

    @Test
    void updateUser_notFound_throwsException() {
        when(entityManager.find(User.class, 1L)).thenReturn(null);
        UserDTO updateDto = new UserDTO();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, updateDto));
        assertTrue(ex.getMessage().contains("Пользователь не найден"));
    }

    @Test
    void searchUser_success() {
        TypedQuery<User> mockedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(mockedQuery);
        when(mockedQuery.setParameter(anyInt(), any())).thenReturn(mockedQuery);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(mockedQuery.getResultList()).thenReturn(List.of(user1, user2));
        when(userMapper.toDTO(user1)).thenReturn(UserDTO.builder().id(1L).build());
        when(userMapper.toDTO(user2)).thenReturn(UserDTO.builder().id(2L).build());

        List<UserDTO> results = userService.searchUsers("Иван", null, null, null);

        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());
        assertEquals(2, results.size());
        verify(entityManager).createQuery(startsWith("SELECT u FROM User u WHERE 1=1"), eq(User.class));
        verify(mockedQuery).setParameter(eq(1), eq("Иван"));
    }
}
