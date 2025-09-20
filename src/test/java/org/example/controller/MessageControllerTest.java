package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.MessageDTO;
import org.example.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendMessage() throws Exception {
        MessageDTO input = MessageDTO.builder().content("Сообщение!").build();
        MessageDTO output = MessageDTO.builder().id(1L).content("Сообщение!").build();

        when(messageService.sendMessage(1L, 1L, any(MessageDTO.class))).thenReturn(output);

        mockMvc.perform(post("/messages/send/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Сообщение!"));

        verify(messageService, times(1)).sendMessage(1L, 1L, any(MessageDTO.class));
    }

    @Test
    void getMessageById_success() throws Exception {
        MessageDTO message = MessageDTO.builder().id(1L).content("Сообщение!").build();
        when(messageService.getMessageById(1L)).thenReturn(Optional.of(message));

        mockMvc.perform(get("/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Сообщение!"));

        verify(messageService, times(1)).getMessageById(1L);
    }

    @Test
    void getMessagesBetweenUsers_success() throws Exception {
        MessageDTO message1 = MessageDTO.builder().id(1L).content("Первое сообщение!").build();
        MessageDTO message2 = MessageDTO.builder().id(2L).content("Второе сообщение!").build();

        when(messageService.getMessageBetweenUsers(1L, 1L)).thenReturn(List.of(message1, message2));

        mockMvc.perform(get("/messages/between/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Первое сообщение!"))
                .andExpect(jsonPath("$[1].content").value("Второе сообщение!"));

        verify(messageService, times(1)).getMessageBetweenUsers(1L, 1L);
    }

    @Test
    void getMessagesForUser_success() throws Exception {
        MessageDTO message1 = MessageDTO.builder().id(1L).content("Первое сообщение!").build();
        MessageDTO message2 = MessageDTO.builder().id(2L).content("Второе сообщение!").build();

        when(messageService.getAllMessagesForUser(1L)).thenReturn(List.of(message1, message2));

        mockMvc.perform(get("/messages/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Первое сообщение!"))
                .andExpect(jsonPath("$[1].content").value("Второе сообщение!"));

        verify(messageService, times(1)).getAllMessagesForUser(1L);
    }

    @Test
    void deleteMessages_success() throws Exception {
        doNothing().when(messageService).deleteMessage(1L);

        mockMvc.perform(delete("/messages/1"))
                .andExpect(status().isNoContent());

        verify(messageService, times(1)).deleteMessage(1L);
    }
}
