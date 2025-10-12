package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CommunityDTO;
import org.example.dto.PostDTO;
import org.example.dto.UserDTO;
import org.example.entity.Community;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.mapper.CommunityMapper;
import org.example.mapper.UserMapper;
import org.example.service.CommunityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommunityServiceImpl implements CommunityService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CommunityMapper communityMapper;
    private final UserMapper userMapper;

    @Override
    public final CommunityDTO createCommunity(Long adminId, String name, String description) {
        User admin = entityManager.find(User.class, adminId);

        if (admin == null) {
            log.error("Error: администратор с id {} не найден", adminId);
            throw new IllegalArgumentException("Администратор не найден");
        }

        Community community = Community.builder()
                .admin(admin)
                .name(name)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(community);
        log.info("Info: сообщество '{}' создано администратором с id {}", name, adminId);

        return communityMapper.toDTO(community);
    }

    @Override
    public void deleteCommunity(Long communityId, Long adminId) {
        Community community = entityManager.find(Community.class, communityId);

        if (community == null) {
            log.error("Error: сообщество с id = {} не найдено", communityId);
            throw new IllegalArgumentException("Сообщество не найдено");
        }

        if (!community.getAdmin().getId().equals(adminId)) {
            log.error("Error: пользователь с id = {} не является администратором сообщества {}", adminId, communityId);
            throw new IllegalArgumentException("Только администратор может удалить сообщество");
        }

        entityManager.remove(community);
        log.info("Info: сообщество с id = {} удалено", communityId);
    }

    @Override
    public void joinCommunity(Long communityId, Long userId) {
        Community community = entityManager.find(Community.class, communityId);
        User user = entityManager.find(User.class, userId);

        if (community == null) {
            log.error("Error: сообщество с id {} не найдено", communityId);
            throw new IllegalArgumentException("Сообщество не найдено");
        }

        if (user == null) {
            log.error("Error: пользователь с id {} не найден", communityId);
            throw new IllegalArgumentException("Пользователь не найден");
        }

        community.getMembers().add(user);
        entityManager.merge(community);
        log.info("Info: пользователь {} присоединился к сообществу {}", userId, communityId);
    }

    @Override
    public void leaveCommunity(Long communityId, Long userId) {
        Community community = entityManager.find(Community.class, communityId);
        User user = entityManager.find(User.class, userId);

        if (community == null) {
            log.error("Error: сообщество с id {} не найдено", communityId);
            throw new IllegalArgumentException("Сообщество не найдено");
        }

        if (user == null) {
            log.error("Error: пользователь с id {} не найден", communityId);
            throw new IllegalArgumentException("Пользователь не найден");
        }

        community.getMembers().remove(user);

        entityManager.merge(community);
        log.info("info: пользователь {} покинул сообщество {}", userId, communityId);
    }

    @Override
    public List<CommunityDTO> getAllCommunities() {
        List<Community> communities = entityManager.createQuery("SELECT c FROM Community c", Community.class).getResultList();

        List<CommunityDTO> result = communities.stream()
                .map(communityMapper::toDTO)
                .toList();

        log.info("Info: найдено всего сообществ: {}", result.size());
        return result;
    }

    @Override
    public List<CommunityDTO> getUserCommunities(Long userId) {
        List<Community> communities = entityManager.createQuery("SELECT c FROM Community c JOIN c.members m WHERE m.id = :userId", Community.class)
                .setParameter("userId", userId)
                .getResultList();

        List<CommunityDTO> result = communities.stream()
                .map(communityMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Info: количество сообществ пользователя {} = {}", userId, result.size());
        return result;
    }

    @Override
    public List<UserDTO> getCommunityMembers(Long communityId) {
        Community community = entityManager.find(Community.class, communityId);
        if (community == null) {
            log.error("Error: сообщество {} не найдено", communityId);
            throw new IllegalArgumentException("Сообщество не найдено");
        }

        List<UserDTO> result = community.getMembers().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Info: количество участников сообщества {}", result.size());

        return result;
    }

    @Override
    public void addPostToCommunity(Long communityId, Long userId, String content) {
        Community community = entityManager.find(Community.class, communityId);
        User user = entityManager.find(User.class, userId);

        if (content == null) {
            log.error("Error: содержимое не может быть пустым");
            throw new IllegalArgumentException("Содержимое не может быть пустым");
        }

        if (community == null) {
            log.error("Error: сообщество {} не найдено", communityId);
            throw new IllegalArgumentException("Сообщество не найдено");
        }

        if (user == null) {
            log.error("Error: пользователь с id {} не найден", communityId);
            throw new IllegalArgumentException("Пользователь не найден");
        }

        Post post = new Post();
        post.setCommunity(community);
        post.setAuthor(user);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());

        community.getPosts().add(post);

        entityManager.persist(post);
        entityManager.merge(community);

        log.info("Info: новый пост {} добавлен в сообщество {}", post.getId(), communityId);
    }

    @Override
    public CommunityDTO updateCommunity(Long communityId, Long adminId, CommunityDTO updatedDTO) {
        Community community = entityManager.find(Community.class, communityId);

        if (community == null) {
            log.error("Error: сообщество {} не найдено", communityId);
            throw new IllegalArgumentException("Сообщество не найдено");
        }

        if(!community.getAdmin().getId().equals(adminId)) {
            log.error("Error: пользователь {} не является администратором сообщества {}", adminId, communityId);
            throw new IllegalArgumentException("Пользователь не является администратором");
        }

        if (updatedDTO.getName() != null && !updatedDTO.getName().isBlank()) {
            community.setName(updatedDTO.getName().trim());
        }
        if (updatedDTO.getDescription() != null && !updatedDTO.getDescription().isBlank()) {
            community.setDescription(updatedDTO.getDescription().trim());
        }

        entityManager.merge(community);
        log.info("Info: сообщество {} обновлено", communityId);
        return communityMapper.toDTO(community);
    }
}
