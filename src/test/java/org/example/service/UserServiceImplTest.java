package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_success() {
        UserDTO inputDto = UserDTO.builder().firstName("Иван").build();
        User userEntity = new User();
        userEntity.setLastName("Иван");
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

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getFirstName(), result.getFirstName());
        verify(entityManager).persist(userEntity);
        verify(entityManager).persist(userEntity);
        veify(userMapper).toDTO(userEntity);
    }
}
