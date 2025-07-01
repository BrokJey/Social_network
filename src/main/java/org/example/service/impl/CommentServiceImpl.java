package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CommentDTO;
import org.example.entity.Comment;
import org.example.entity.Post;
import org.example.entity.User;
import org.example.mapper.CommentMapper;
import org.example.service.CommentService;
import org.mapstruct.control.MappingControl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final CommentMapper commentMapper;

    @Override
    public CommentDTO addComment(CommentDTO commentDTO) {
        if (commentDTO.getContent() == null || commentDTO.getContent().isBlank()) {
            log.error("Error: комментарий пустой {}", commentDTO.getId());
            throw new IllegalArgumentException("Комментарий не может быть пустым");
        }

        User author = entityManager.find(User.class, commentDTO.getAuthorId());
        Post post = entityManager.find(Post.class, commentDTO.getPostId());

        if (author == null || post == null) {
            log.error("Error: автор или пост не найдены (authorId = {}, postId = {})", commentDTO.getAuthorId(), commentDTO.getPostId());
            throw new IllegalArgumentException("Автор или пост не найдены");
        }

        Comment comment = Comment.builder()
                .content(commentDTO.getContent().trim())
                .author(author)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(comment);
        log.info("Info: комментарий добавлен пользователем {} к посту {}", author.getId(), post.getId());

        return commentMapper.toDTO(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long postId) {
        Comment comment = entityManager.find(Comment.class, commentId);

        if (comment == null) {
            log.error("Error: комментарий с id = {} не найден", commentId);
            throw new IllegalArgumentException("Комментарий не найден");
        }

        if (comment.getPost().getId() != postId) {
            log.error("Error: комментарий {} не принадлежит посту {}", commentId, postId);
            throw new IllegalArgumentException("Комментарий не принадлежит указанному посту");
        }

        entityManager.remove(comment);
        log.info("Info: комментарий с id = {} удален", commentId);
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = entityManager.createQuery("SELECT c FROM Comment c JOIN c.post p WHERE p.id = :postId", Comment.class)
                .setParameter("postId", postId)
                .getResultList();

        List<CommentDTO> result = comments.stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Info: количество комментариев к посту {} = {}", postId, result.size());
        return result;
    }
}
