package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CommunityDTO;
import org.example.dto.UserDTO;
import org.example.service.CommunityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityController.class)
public class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommunityService communityService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createCommunity_success() throws Exception {
        CommunityDTO output = CommunityDTO.builder().id(1L).name("Сообщество").adminId(2L).description("Хорошего дня!").build();

        when(communityService.createCommunity(2L, "Сообщество", "Хорошего дня!")).thenReturn(output);

        mockMvc.perform(post("/community/create/{admin}", 2L)
                        .param("name", "Сообщество")
                        .param("description", "Хорошего дня!")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.adminId").value(2))
                .andExpect(jsonPath("$.name").value("Сообщество"))
                .andExpect(jsonPath("$.description").value("Хорошего дня!"));

        verify(communityService, times(1)).createCommunity(2L, "Сообщество", "Хорошего дня!");
    }

    @Test
    void deleteCommunity_success() throws Exception {
        CommunityDTO community = CommunityDTO.builder().id(1L).name("Сообщество").adminId(2L).build();
        doNothing().when(communityService).deleteCommunity(1L, 2L);

        mockMvc.perform(delete("/community/delete/1")
                .param("adminId", "2"))
                .andExpect(status().isNoContent());

        verify(communityService, times(1)).deleteCommunity(1L, 2L);
    }

    @Test
    void joinCommunity_success() throws Exception {
        mockMvc.perform(post("/community/join")
                .param("communityId", "1")
                .param("userId", "1"))
                .andExpect(status().isOk());

        verify(communityService, times(1)).joinCommunity(1L, 1L);
    }

    @Test
    void leaveCommunity_success() throws Exception {
        mockMvc.perform(post("/community/leave")
                .param("communityId", "1")
                .param("userId", "1"))
                .andExpect(status().isOk());

        verify(communityService, times(1)).leaveCommunity(1L, 1L);
    }

    @Test
    void getAllCommunities_success() throws Exception {
        CommunityDTO community1 = CommunityDTO.builder().id(1L).name("Сообщество1").build();
        CommunityDTO community2 = CommunityDTO.builder().id(2L).name("Сообщество2").build();

        when(communityService.getAllCommunities()).thenReturn(List.of(community1, community2));

        mockMvc.perform(get("/community/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Сообщество1"))
                .andExpect(jsonPath("$[1].name").value("Сообщество2"));
    }

    @Test
    void getUserCommunities_success() throws Exception {
        CommunityDTO community1 = CommunityDTO.builder().id(1L).name("Сообщество1").build();
        CommunityDTO community2 = CommunityDTO.builder().id(2L).name("Сообщество2").build();

        when(communityService.getUserCommunities(1L)).thenReturn(List.of(community1, community2));

        mockMvc.perform(get("/community/show/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Сообщество1"))
                .andExpect(jsonPath("$[1].name").value("Сообщество2"));
    }

    @Test
    void getCommunityMembers_success() throws Exception {
        UserDTO user1 = UserDTO.builder().id(1L).firstName("Иван").build();
        UserDTO user2 = UserDTO.builder().id(2L).firstName("Андрей").build();

        when(communityService.getCommunityMembers(1L)).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/community/members/{communityId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void addPostToCommunity_success() throws Exception {
        doNothing().when(communityService).addPostToCommunity(1L, 2L, "Новый пост");

        mockMvc.perform(post("/community/post/{communityId}", 1L)
                .param("userId", "2")
                .param("content", "Новый пост"))
                .andExpect(status().isOk());

        verify(communityService, times(1)).addPostToCommunity(1L, 2L, "Новый пост");
    }

    @Test
    void updateCommunity_success() throws Exception {
        CommunityDTO updateDto = CommunityDTO.builder().name("Сообщество").build();
        CommunityDTO returnedDto = CommunityDTO.builder().id(1L).name("Сообщество").build();

        when(communityService.updateCommunity(eq(1L), eq(1L), any(CommunityDTO.class))).thenReturn(returnedDto);

        mockMvc.perform(post("/community/update/{communityId}", 1L)
                        .param("adminId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Сообщество"));

        verify(communityService, times(1)).updateCommunity(eq(1L), eq(1L), any(CommunityDTO.class));

    }
}
