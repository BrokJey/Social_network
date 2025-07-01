package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PostDTO;
import org.example.entity.Community;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.mapper.PostMapper;
import org.example.service.PostService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final PostMapper postMapper;

    @Override
    public PostDTO createPost(Long userId, PostDTO postDTO) {
        if (postDTO.getContent() == null) {
            log.error("Error: содержимое поста не может быть пустым");
            throw new IllegalArgumentException("Содержимое поста не может быть пустым");
        }

        User user = entityManager.find(User.class, userId);
        if (user == null) {
            log.error("Error: пользователь с id {} не найден", userId);
        }

        Post post = postMapper.fromDTO(postDTO);
        post.setAuthor(user);
        post.setCreatedAt(LocalDateTime.now());
        entityManager.persist(post);
        log.info("Info: создан пост с id {} для пользователя {}", post.getId(), userId);
        return postMapper.toDTO(post);
    }

    @Override
    public Optional<PostDTO> getPostById(Long postId) {
        Post post = entityManager.find(Post.class, postId);
        if (post != null) {
            log.info("Info: Пост найден с id {}", post.getId());
            return Optional.of(postMapper.toDTO(post));
        }
        log.error("Error: Пост не найден с id {}", postId);
        return Optional.empty();
    }

    @Override
    public List<PostDTO> getPostsByUserId(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            log.error("Error: пользователь с id {} не найден", userId);
            throw new IllegalArgumentException("Пользователь не найден");
        }

        List<Post> posts = entityManager.createQuery("SELECT p FROM Post p WHERE p.author.id = :userId", Post.class).setParameter("userId", userId).getResultList();

        List<PostDTO> result = posts.stream()
                .map(postMapper::toDTO)
                .toList();

        log.info("Info: найдено постов пользователя {}: {}", userId, result.size());
        return result;
    }

    @Override
    public List<PostDTO> getPostsByCommunityId(Long communityId) {
        Community community = entityManager.find(Community.class, communityId);
        if (community == null) {
            log.error("Error: сообщество с id {} не найдено", communityId);
            throw new IllegalArgumentException("Сообщество не найдено");
        }

        List<Post> posts = entityManager.createQuery("SELECT p FROM Post p WHERE p.community.id = :communityId", Post.class).setParameter("communityId", communityId).getResultList();

        List<PostDTO> result = posts.stream()
                .map(postMapper::toDTO)
                .toList();

        log.info("Info: найдено постов сообщества {}: {}", communityId, result.size());
        return result;
    }

    @Override
    public List<PostDTO> getAllPosts() {
        List<Post> posts = entityManager.createQuery("SELECT p FROM Post p", Post.class).getResultList();

        List<PostDTO> result = posts.stream()
                .map(postMapper::toDTO)
                .toList();

        log.info("Info: найдено всего постов: {}", result.size());
        return result;
    }

    @Override
    public PostDTO updatePost(Long postId, PostDTO updatedPost) {
        Post post = entityManager.find(Post.class, postId);
        if(post == null) {
            log.error("Error: пост не найден с id {}", postId);
            throw new IllegalArgumentException("пост не найден с id: " + postId);
        }

        post.setContent(updatedPost.getContent());

        entityManager.merge(post);
        log.info("Info: пост с id {} обновлен", post.getId());
        return postMapper.toDTO(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = entityManager.find(Post.class, postId);
        if (post != null) {
            log.info("Info: пост удален: id {}", post.getId());
            entityManager.remove(post);
        }
        else {
            log.error("Error: пост с id {} не найден и не может быть удалён", postId);
        }
    }
}
