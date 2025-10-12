package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ChatDTO;
import org.example.entity.enums.ChatType;
import org.example.service.ChatService;
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
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    void createChat_success() throws Exception {
        ChatDTO output = ChatDTO.builder().id(1L).type(ChatType.PRIVATE).build();

        when(chatService.createChat(1L, 2L)).thenReturn(output);

        mockMvc.perform(post("/chat/create/{user1}/{user2}", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("PRIVATE"));

        verify(chatService, times(1)).createChat(1L, 2L);
    }

    @Test
    void createGroupChat_success() throws Exception {
        ChatDTO output = ChatDTO.builder().id(1L).type(ChatType.GROUP).participantsIds(Set.of(1L, 2L, 3L)).build();

        when(chatService.createGroupChat(Set.of(1L, 2L, 3L))).thenReturn(output);

        mockMvc.perform(post("/chat/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Set.of(1L, 2L, 3L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("GROUP"))
                .andExpect(jsonPath("$.participantsIds.length()").value(3))
                .andExpect(jsonPath("$.participantsIds").isArray());

        verify(chatService, times(1)).createGroupChat(Set.of(1L, 2L, 3L));
    }

    @Test
    void getUserChats_success() throws Exception {
        ChatDTO chat1 = ChatDTO.builder().id(1L).type(ChatType.PRIVATE).build();
        ChatDTO chat2 = ChatDTO.builder().id(2L).type(ChatType.GROUP).build();

        when(chatService.getUserChats(1L)).thenReturn(List.of(chat1, chat2));

        mockMvc.perform(get("/chat/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("PRIVATE"))
                .andExpect(jsonPath("$[1].type").value("GROUP"));

        verify(chatService, times(1)).getUserChats(1L);
    }

    @Test
    void deleteChat_success() throws Exception {
        ChatDTO chat = ChatDTO.builder().id(1L).type(ChatType.PRIVATE).build();
        doNothing().when(chatService).deleteChat(1L, 1L);

        mockMvc.perform(delete("/chat/{id}", 1L)
                .param("requesterId", "1"))
                .andExpect(status().isNoContent());

        verify(chatService, times(1)).deleteChat(1L, 1L);
    }
}
