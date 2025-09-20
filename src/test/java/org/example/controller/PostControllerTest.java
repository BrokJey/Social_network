package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.PostDTO;
import org.example.service.PostService;
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

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createPostUser_success() throws Exception {
        PostDTO input = PostDTO.builder().content("Пост!").build();
        PostDTO output = PostDTO.builder().id(1L).content("Пост!").build();

        when(postService.createPostUser(1L, any(PostDTO.class))).thenReturn(output);

        mockMvc.perform(post("/posts/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Пост!"));

        verify(postService, times(1)).createPostUser(1L, any(PostDTO.class));
    }

    @Test
    void createPostCommunity_success() throws Exception {
        PostDTO input = PostDTO.builder().content("Пост!").build();
        PostDTO output = PostDTO.builder().id(1L).content("Пост!").build();

        when(postService.createPostCommunity(1L, any(PostDTO.class))).thenReturn(output);

        mockMvc.perform(post("/posts/community/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Пост!"));

        verify(postService, times(1)).createPostCommunity(1L, any(PostDTO.class));

    }

    @Test
    void getPostById_success() throws Exception {
        PostDTO post = PostDTO.builder().id(1L).content("Пост!").build();
        when(postService.getPostById(1L)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Пост!"));

        verify(postService, times(1)).getPostById(1L);
    }

    @Test
    void getPostByUserId_success() throws Exception {
        PostDTO post1 = PostDTO.builder().id(1L).content("Первый пост!").build();
        PostDTO post2 = PostDTO.builder().id(2L).content("Второй пост!").build();

        when(postService.getPostsByUserId(1L)).thenReturn(List.of(post1, post2));

        mockMvc.perform(get("/posts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Первый пост!")) // Полная проверка данных
                .andExpect(jsonPath("$[1].content").value("Второй пост!"));

        verify(postService, times(1)).getPostsByUserId(1L);
    }

    @Test
    void getPostByCommunityId_success() throws Exception {
        PostDTO post1 = PostDTO.builder().id(1L).content("Первый пост!").build();
        PostDTO post2 = PostDTO.builder().id(2L).content("Второй пост!").build();

        when(postService.getPostsByCommunityId(1L)).thenReturn(List.of(post1, post2));

        mockMvc.perform(get("/posts/community/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Первый пост!")) // Полная проверка данных
                .andExpect(jsonPath("$[1].content").value("Второй пост!"));

        verify(postService, times(1)).getPostsByCommunityId(1L);
    }

    @Test
    void getAllPosts_success() throws Exception {
        PostDTO post1 = PostDTO.builder().id(1L).content("Первый пост!").build();
        PostDTO post2 = PostDTO.builder().id(2L).content("Второй пост!").build();

        when(postService.getAllPosts()).thenReturn(List.of(post1, post2));

        mockMvc.perform(get("/posts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(postService, times(1)).getAllPosts();
    }

    @Test
    void updatePost_success() throws Exception {
        PostDTO updateDto = PostDTO.builder().content("Пост!").build();
        PostDTO returnedDto = PostDTO.builder().id(1L).content("Пост!").build();

        when(postService.updatePost(eq(1L), any(PostDTO.class))).thenReturn(returnedDto);

        mockMvc.perform(put("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Пост!"));

        verify(postService, times(1)).updatePost(eq(1L), any(PostDTO.class));
    }

    @Test
    void deletePost_success() throws Exception {
        PostDTO post = PostDTO.builder().id(1L).content("Пост!").build();
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).deletePost(1L);
    }
}
