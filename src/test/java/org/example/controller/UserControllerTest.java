package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void createUser_success() throws Exception {
        UserDTO input = UserDTO.builder().firstName("Иван").build();
        UserDTO output = UserDTO.builder().id(1L).firstName("Иван").build();

        when(userService.registerUser(any(UserDTO.class))).thenReturn(output);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }

    @Test
    void getUserById_success() throws Exception {
        UserDTO user = UserDTO.builder().id(1L).firstName("Иван").build();
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }

    @Test
    void updateUser_success() throws Exception {
        UserDTO updateDto = UserDTO.builder().firstName("Андрей").build();
        UserDTO returnedDto = UserDTO.builder().id(1L).firstName("Андрей").build();

        when(userService.updateUser(eq(1L),any(UserDTO.class))).thenReturn(returnedDto);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Андрей"));
    }

    @Test
    void searchUsers_success() throws  Exception {
        UserDTO user1 = UserDTO.builder().id(1L).firstName("Иван").build();
        UserDTO user2 = UserDTO.builder().id(2L).firstName("Андрей").build();

        when(userService.searchUsers("Иван", null, null, null))
                .thenReturn(List.of(user1));
        when(userService.searchUsers(null, null, null, null))
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users/search")
                .param("firstName", "Иван"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Иван"));

        mockMvc.perform(get("/users/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
