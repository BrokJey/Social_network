package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.CommentDTO;
import org.example.entity.Comment;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.mapper.CommentMapper;
import org.example.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TypedQuery<Comment> query;

    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с EntityManager и маппером
        commentService = new CommentServiceImpl(entityManager, commentMapper);
    }

    @Test
    void addComment_success() {
        Post post = new Post();
        post.setId(1L);
        User user = new User();
        user.setId(1L);
        CommentDTO inputDto = CommentDTO.builder().content("Комментарий").authorId(1L).postId(1L).build();

        CommentDTO expectedDto = CommentDTO.builder().id(1L).content("Комментарий").authorId(1L).postId(1L).build();

        // Мокаем find() методы
        when(entityManager.find(User.class, 1L)).thenReturn(user);
        when(entityManager.find(Post.class, 1L)).thenReturn(post);

        doAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(1L);
            return null;
        }).when(entityManager).persist(any(Comment.class));

        when(commentMapper.toDTO(any(Comment.class))).thenReturn(expectedDto);

        CommentDTO result = commentService.addComment(inputDto);

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getContent(), result.getContent());
        assertEquals(expectedDto.getPostId(), result.getPostId());
        assertEquals(expectedDto.getAuthorId(), result.getAuthorId());
        verify(entityManager).persist(any(Comment.class));
        verify(commentMapper).toDTO(any(Comment.class));

    }

    @Test
    void deleteComment_success() {
        Post post = new Post();
        post.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Комментарий");
        comment.setPost(post);

        when(entityManager.find(Comment.class, 1L)).thenReturn(comment);

        commentService.deleteComment(1L, 1L);

        verify(entityManager).find(Comment.class, 1L);
        verify(entityManager).remove(comment);
    }

    @Test
    void getCommentsByPostId_success() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("Комментарий1");

        Comment comment2 = new Comment();
        comment2.setId(1L);
        comment2.setContent("Комментарий2");

        List<Comment> comments = List.of(comment1, comment2);

        when(entityManager.createQuery(anyString(), eq(Comment.class))).thenReturn(query);
        when(query.setParameter(eq("postId"), eq(1L))).thenReturn(query);
        when(query.getResultList()).thenReturn(comments);

        when(commentMapper.toDTO(comment1)).thenReturn(CommentDTO.builder().id(1L).content("Комментарий1").build());
        when(commentMapper.toDTO(comment2)).thenReturn(CommentDTO.builder().id(2L).content("Комментарий2").build());

        List<CommentDTO> result = commentService.getCommentsByPostId(1L);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(2, result.size(), "Должно вернуть 2 комментария");

        assertEquals(1L, result.get(0).getId());
        assertEquals("Комментарий1", result.get(0).getContent());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Комментарий2", result.get(1).getContent());
    }
}
