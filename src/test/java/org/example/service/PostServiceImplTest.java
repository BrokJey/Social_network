package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.CommunityDTO;
import org.example.dto.PostDTO;
import org.example.dto.UserDTO;
import org.example.entity.Community;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.mapper.PostMapper;
import org.example.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.parameters.P;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PostServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPostUser_success() {
        PostDTO inputDto = PostDTO.builder().content("Новый пост!").build();
        Post postEntity = new Post();
        postEntity.setContent("Новый пост!");
        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setContent("Новый пост!");

        PostDTO exceptedDto = PostDTO.builder().id(1L).content("Новый пост!").build();

        when(postMapper.fromDTO(inputDto)).thenReturn(postEntity);
        doAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setId(1L);
            return null;
        }).when(entityManager).persist(postEntity);

        when(postMapper.toDTO(any(Post.class))).thenReturn(exceptedDto);

        PostDTO result = postService.createPostUser(1L, inputDto);

        assertEquals(exceptedDto.getId(), result.getId());
        assertEquals(exceptedDto.getContent(), result.getContent());
        verify(entityManager).persist(postEntity);
        verify(postMapper).toDTO(any(Post.class));
    }

    @Test
    void createPostUser_nullName_throwsException() {
        PostDTO inputDTO = PostDTO.builder().content(null).build();
        Post postEntity = new Post();
        postEntity.setContent("Новый пост!");
        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setContent("Новый пост!");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> postService.createPostUser(1L, inputDTO));
        assertEquals("Error: содержимое поста не может быть пустым", ex.getMessage());
        verifyNoInteractions(entityManager, postMapper);
    }

    @Test
    void createPostCommunity_success() {
        PostDTO inputDto = PostDTO.builder().content("Новый пост!").build();
        Post postEntity = new Post();
        postEntity.setContent("Новый пост!");
        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setContent("Новый пост!");

        PostDTO exceptedDto = PostDTO.builder().id(1L).content("Новый пост!").build();

        when(postMapper.fromDTO(inputDto)).thenReturn(postEntity);
        doAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setId(1L);
            return null;
        }).when(entityManager).persist(postEntity);

        when(postMapper.toDTO(any(Post.class))).thenReturn(exceptedDto);

        PostDTO result = postService.createPostCommunity(1L, inputDto);

        assertEquals(exceptedDto.getId(), result.getId());
        assertEquals(exceptedDto.getContent(), result.getContent());
        verify(entityManager).persist(postEntity);
        verify(postMapper).toDTO(any(Post.class));
    }

    @Test
    void createPostCommunity_nullName_throwsException() {
        PostDTO inputDTO = PostDTO.builder().content(null).build();
        Post postEntity = new Post();
        postEntity.setContent("Новый пост!");
        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setContent("Новый пост!");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> postService.createPostCommunity(1L, inputDTO));
        assertEquals("Error: содержимое поста не может быть пустым", ex.getMessage());
        verifyNoInteractions(entityManager, postMapper);
    }

    @Test
    void getPostById_found() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Новый пост!");

        PostDTO dto = PostDTO.builder().id(1L).content("Новый пост!").build();

        when(entityManager.find(Post.class, 1L)).thenReturn(post);
        when(postMapper.toDTO(post)).thenReturn(dto);

        PostDTO result = postService.getPostById(1L).orElse(null);

        assertEquals(dto.getId(), result.getId());
        verify(entityManager).find(Post.class, 1L);
        verify(postMapper).toDTO(post);
    }

    @Test
    void getPostsByUserId_success() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Первый пост!");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("Второй пост!");

        List<Post> posts = List.of(post1, post2);

        TypedQuery<Post> queryMock = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Post.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("userId"), eq(1L))).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(posts);

        when(postMapper.toDTO(post1)).thenReturn(PostDTO.builder().id(1L).content("Первый пост!").build());
        when(postMapper.toDTO(post2)).thenReturn(PostDTO.builder().id(2L).content("Второй пост!").build());

        List<PostDTO> result = postService.getPostsByUserId(1L);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(2, result.size(), "Должно вернуть 2 поста");

        assertEquals(1L, result.get(0).getId());
        assertEquals("Первый пост!", result.get(0).getContent());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Второй пост!", result.get(1).getContent());

        verify(entityManager, times(1)).find(User.class, 1L);
    }

    @Test
    void getPostsByCommunityId_success() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Первый пост!");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("Второй пост!");

        List<Post> posts = List.of(post1, post2);

        TypedQuery<Post> queryMock = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Post.class))).thenReturn(queryMock);
        when(queryMock.setParameter(eq("communityId"), eq(1L))).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(posts);

        when(postMapper.toDTO(post1)).thenReturn(PostDTO.builder().id(1L).content("Первый пост!").build());
        when(postMapper.toDTO(post2)).thenReturn(PostDTO.builder().id(2L).content("Второй пост!").build());

        List<PostDTO> result = postService.getPostsByCommunityId(1L);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(2, result.size(), "Должно вернуть 2 поста");

        assertEquals(1L, result.get(0).getId());
        assertEquals("Первый пост!", result.get(0).getContent());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Второй пост!", result.get(1).getContent());

        verify(entityManager, times(1)).find(Community.class, 1L);
    }

    @Test
    void getAllPosts_success() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Пост 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("Пост 2");

        List<Post> posts = List.of(post1, post2);

        TypedQuery<Post> queryMock = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM Post p", Post.class)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(posts);
        when(postMapper.toDTO(post1)).thenReturn(PostDTO.builder().id(1L).content("Пост 1").build());
        when(postMapper.toDTO(post2)).thenReturn(PostDTO.builder().id(2L).content("Пост 2").build());

        List<PostDTO> result = postService.getAllPosts();

        assertEquals(2, result.size());
        verify(postMapper).toDTO(post1);
        verify(postMapper).toDTO(post2);
    }

    @Test
    void updatePost_success() {
        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setContent("Old");

        PostDTO updateDto = PostDTO.builder()
                .content("New")
                .build();

        when(entityManager.find(Post.class, 1L)).thenReturn(existingPost);
        when(postMapper.toDTO(existingPost)).thenReturn(PostDTO.builder().id(1L).content("New").build());

        PostDTO updated = postService.updatePost(1L, updateDto);

        assertEquals("New", existingPost.getContent());
        assertEquals(1L, updated.getId());
        assertEquals("New", updated.getContent());
        verify(entityManager).merge(existingPost);

    }

    @Test
    void updatePost_notFound_throwsException() {
        when(entityManager.find(Post.class, 1L)).thenReturn(null);
        PostDTO updateDto = new PostDTO();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> postService.updatePost(1L, updateDto));
        assertEquals("пост не найден с id: 1", ex.getMessage());
    }

    @Test
    void deletePost_success() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Пост");

        when(entityManager.find(Post.class, 1L)).thenReturn(post);

        postService.deletePost(1L);

        verify(entityManager).find(Post.class, 1L);
        verify(entityManager).remove(post);
    }
}
