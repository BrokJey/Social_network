package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.FriendshipDTO;
import org.example.dto.UserDTO;
import org.example.entity.enums.FriendshipStatus;
import org.example.service.FriendshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FriendshipService friendshipService;

    @InjectMocks
    private FriendshipController friendshipController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(friendshipController).build();
    }

    @Test
    void sendFriendRequest() throws Exception {
        FriendshipDTO output = FriendshipDTO.builder().id(1L).status(FriendshipStatus.PENDING).build();

        when(friendshipService.sendFriendRequest(1L, 2L)).thenReturn(output);

        mockMvc.perform(post("/friendships/request/{sender}/{receiver}", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(friendshipService, times(1)).sendFriendRequest(1L, 2L);
    }

    @Test
    void acceptFriendRequest_success() throws Exception {
        mockMvc.perform(post("/friendships/accept/{id}", 1L))
                .andExpect(status().isOk());

        verify(friendshipService, times(1)).acceptFriendRequest(1L);
    }

    @Test
    void declineFriendRequest_success() throws Exception {
        mockMvc.perform(delete("/friendships/decline/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(friendshipService, times(1)).declineFriendRequest(1L);
    }

    @Test
    void removeFriend_success() throws Exception {
        mockMvc.perform(delete("/friendships/remove")
                .param("userId", "1")
                .param("friendId", "2"))
                .andExpect(status().isNoContent());

        verify(friendshipService, times(1)).removeFriend(1L, 2L);
    }

    @Test
    void getFriends_success() throws Exception {
        UserDTO user2 = UserDTO.builder().id(2L).firstName("Настя").build();

        when(friendshipService.getFriends(1L)).thenReturn(List.of(user2));

        mockMvc.perform(get("/friendships/get/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Настя"));

        verify(friendshipService, times(1)).getFriends(1L);
    }

    @Test
    void getPendingRequests_success() throws Exception {
        FriendshipDTO dto = FriendshipDTO.builder().id(2L).status(FriendshipStatus.PENDING).build();
        when(friendshipService.getPendingRequests(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/friendships/pending/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(friendshipService, times(1)).getPendingRequests(1L);
    }
}
