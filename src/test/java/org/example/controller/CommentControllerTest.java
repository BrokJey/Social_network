package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CommentDTO;
import org.example.service.CommentService;
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
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void addComment_success() throws Exception {
        CommentDTO input = CommentDTO.builder().content("Комментарий").build();
        CommentDTO output = CommentDTO.builder().id(1L).content("Комментарий").build();

        when(commentService.addComment(any(CommentDTO.class))).thenReturn(output);

        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Комментарий"));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
    }

    @Test
    void deleteComment_success() throws Exception {
        doNothing().when(commentService).deleteComment(1L, 1L);

        mockMvc.perform(delete("/comment/1")
                .param("postId", "1"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1L, 1L);
    }

    @Test
    void getCommentsByPostId_success() throws Exception {
        CommentDTO comment1 = CommentDTO.builder().id(1L).content("Комментарий1").build();
        CommentDTO comment2 = CommentDTO.builder().id(2L).content("Комментарий2").build();

        when(commentService.getCommentsByPostId(1L)).thenReturn(List.of(comment1, comment2));

        mockMvc.perform(get("/comment/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Комментарий1"))
                .andExpect(jsonPath("$[1].content").value("Комментарий2"));

        verify(commentService, times(1)).getCommentsByPostId(1L);
    }
}
