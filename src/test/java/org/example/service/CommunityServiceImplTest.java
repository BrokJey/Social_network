package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.CommunityDTO;
import org.example.dto.UserDTO;
import org.example.entity.Community;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.mapper.CommunityMapper;
import org.example.mapper.UserMapper;
import org.example.service.impl.CommunityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CommunityServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CommunityMapper communityMapper;

    @Mock
    private UserMapper userMapper;

    private CommunityServiceImpl communityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с мапперами (EntityManager не в конструкторе)
        communityService = new CommunityServiceImpl(communityMapper, userMapper);
        
        // Устанавливаем EntityManager через рефлексию
        try {
            java.lang.reflect.Field field = CommunityServiceImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(communityService, entityManager);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось установить EntityManager", e);
        }
    }

    @Test
    void createCommunity_success() {
        User admin = new User();
        admin.setId(1L);
        admin.setFirstName("Иван");
        CommunityDTO inputDto = CommunityDTO.builder().name("Сообщество").adminId(1L).build();
        Community communityEntity = new Community();
        communityEntity.setName("Сообщество");
        communityEntity.setAdmin(admin);
        Community savedCommunity = new Community();
        savedCommunity.setId(1L);
        savedCommunity.setName("Сообщество");
        savedCommunity.setAdmin(admin);


        CommunityDTO expectedDto = CommunityDTO.builder().id(1L).name("Сообщество").adminId(1L).build();

        doAnswer(invocation -> {
            Community c = invocation.getArgument(0);
            c.setId(1L);
            return null;
        }).when(entityManager).persist(any(Community.class));

        when(communityMapper.toDTO(any(Community.class))).thenReturn(expectedDto);
        when(entityManager.find(User.class, 1L)).thenReturn(admin);


        CommunityDTO result = communityService.createCommunity(1L, inputDto.getName(), inputDto.getDescription());

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getAdminId(), result.getAdminId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getDescription(), result.getDescription());
        verify(entityManager).persist(any(Community.class));
        verify(communityMapper).toDTO(any(Community.class));
    }

    @Test
    void deleteCommunity_success() {
        Community community = new Community();
        community.setId(1L);
        community.setName("Сообщество");

        User admin = new User();
        admin.setId(1L);
        community.setAdmin(admin);

        when(entityManager.find(Community.class, 1L)).thenReturn(community);

        communityService.deleteCommunity(1L, 1L);

        verify(entityManager).find(Community.class, 1L);
        verify(entityManager).remove(community);
    }

    @Test
    void joinCommunity_success() {
        Community community = new Community();
        community.setId(1L);
        community.setName("Сообщество");

        User user = new User();
        user.setId(2L);

        when(entityManager.find(User.class, 2L)).thenReturn(user);
        when(entityManager.find(Community.class, 1L)).thenReturn(community);
        when(communityMapper.toDTO(community)).thenReturn(CommunityDTO.builder().id(1L).name("Сообщество").build());

        communityService.joinCommunity(1L, 2L);

        assertEquals(Set.of(user), community.getMembers());
        assertTrue(community.getMembers().contains(user));
        verify(entityManager).merge(community);
    }

    @Test
    void leaveCommunity_success() {
        Community community = new Community();
        community.setId(1L);
        community.setName("Сообщество");

        User user = new User();
        user.setId(2L);
        community.getMembers().add(user);

        when(entityManager.find(User.class, 2L)).thenReturn(user);
        when(entityManager.find(Community.class, 1L)).thenReturn(community);
        when(communityMapper.toDTO(community)).thenReturn(CommunityDTO.builder().id(1L).name("Сообщество").build());

        communityService.leaveCommunity(1L, 2L);

        assertTrue(community.getMembers().isEmpty());
        verify(entityManager).merge(community);
    }

    @Test
    void getAllCommunities_success() {
        Community community1 = new Community();
        community1.setId(1L);
        community1.setName("Сообщество1");

        Community community2 = new Community();
        community2.setId(2L);
        community2.setName("Сообщество2");

        List<Community> communities = List.of(community1, community2);

        TypedQuery<Community> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT c FROM Community c", Community.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(communities);
        when(communityMapper.toDTO(community1)).thenReturn(CommunityDTO.builder().id(1L).name("Сообщество1").build());
        when(communityMapper.toDTO(community2)).thenReturn(CommunityDTO.builder().id(2L).name("Сообщество2").build());

        List<CommunityDTO> result = communityService.getAllCommunities();

        assertEquals(2, result.size());
        verify(communityMapper).toDTO(community1);
        verify(communityMapper).toDTO(community2);
    }

    @Test
    void getUserCommunities_success() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Иван");

        Community community1 = new Community();
        community1.setId(1L);
        community1.setName("Сообщество1");
        community1.setMembers(Set.of(user));

        Community community2 = new Community();
        community2.setId(2L);
        community2.setName("Сообщество2");
        community2.setMembers(Set.of(user));

        List<Community> communities = List.of(community1, community2);

        TypedQuery<Community> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT c FROM Community c JOIN c.members m WHERE m.id = :userId", Community.class)).thenReturn(query);
        when(query.setParameter(eq("userId"), eq(1L))).thenReturn(query);
        when(query.getResultList()).thenReturn(communities);
        when(communityMapper.toDTO(community1)).thenReturn(CommunityDTO.builder().id(1L).name("Сообщество1").build());
        when(communityMapper.toDTO(community2)).thenReturn(CommunityDTO.builder().id(2L).name("Сообщество2").build());

        List<CommunityDTO> result = communityService.getUserCommunities(1L);

        assertEquals(2, result.size());
        verify(communityMapper).toDTO(community1);
        verify(communityMapper).toDTO(community2);
    }

    @Test
    void getCommunityMembers_success() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Иван");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Алина");

        Community community = new Community();
        community.setId(1L);
        community.setName("Сообщество");
        community.setMembers(Set.of(user1, user2));

        when(entityManager.find(Community.class, 1L)).thenReturn(community);
        when(userMapper.toDTO(user1)).thenReturn(UserDTO.builder().id(1L).firstName("Иван").build());
        when(userMapper.toDTO(user2)).thenReturn(UserDTO.builder().id(2L).firstName("Алина").build());

        List<UserDTO> result = communityService.getCommunityMembers(1L);

        assertEquals(2, result.size());
        verify(userMapper).toDTO(user1);
        verify(userMapper).toDTO(user2);
    }

    @Test
    void addPostToCommunity_success() {
        Community community = new Community();
        community.setId(1L);
        community.setName("Сообщество");

        User user = new User();
        user.setId(2L);

        Post post = new Post();
        post.setCommunity(community);
        post.setAuthor(user);
        post.setContent("Пост!");
        post.setCreatedAt(LocalDateTime.now());


        when(entityManager.find(User.class, 2L)).thenReturn(user);
        when(entityManager.find(Community.class, 1L)).thenReturn(community);
        when(communityMapper.toDTO(community)).thenReturn(CommunityDTO.builder().id(1L).name("Сообщество").build());

        communityService.addPostToCommunity(1L, 2L, "Пост!");

        assertEquals(1, community.getPosts().size());
        assertEquals("Пост!", community.getPosts().get(0).getContent());
        assertEquals(user, community.getPosts().get(0).getAuthor());
        verify(entityManager).merge(community);
        verify(entityManager).persist(any(Post.class));

    }

    @Test
    void updateCommunity_success() {
        Community existingCommunity = new Community();
        existingCommunity.setId(1L);
        existingCommunity.setName("Old");

        User admin = new User();
        admin.setId(2L);
        existingCommunity.setAdmin(admin);

        CommunityDTO updateDto = CommunityDTO.builder().name("New").build();

        when(entityManager.find(Community.class, 1L)).thenReturn(existingCommunity);
        when(communityMapper.toDTO(existingCommunity)).thenReturn(updateDto);

        CommunityDTO updated = communityService.updateCommunity(1L, 2L, updateDto);

        assertEquals("New", existingCommunity.getName());
        verify(entityManager).merge(existingCommunity);
        assertEquals(updateDto, updated);
    }
}