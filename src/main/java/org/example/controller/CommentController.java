package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.CommentDTO;
import org.example.security.CustomUserDetails;
import org.example.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //Добавить комментарий
    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO commentDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long authorId = userDetails.getId();
        
        // Устанавливаем автора комментария
        commentDTO.setAuthorId(authorId);
        
        CommentDTO comment = commentService.addComment(commentDTO);
        return ResponseEntity.ok(comment);
    }

    //Удалить комментарий
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId, @RequestParam Long postId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        // Проверяем, что пользователь может удалить этот комментарий
        // (добавить проверку в сервис)
        commentService.deleteComment(commentId, postId);
        return ResponseEntity.noContent().build();
    }

    //Посмотреть комментарии к посту
    @GetMapping("/post/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("id") Long postId) {
        List<CommentDTO> comment = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comment);
    }
}
