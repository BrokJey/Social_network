package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.CommentDTO;
import org.example.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //Добавить комментарий
    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO commentDTO) {
        CommentDTO comment = commentService.addComment(commentDTO);
        return ResponseEntity.ok(comment);
    }

    //Удалить комментарий
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId, @RequestParam Long postId) {
        commentService.deleteComment(commentId, postId);
        return ResponseEntity.noContent().build();
    }

    //Посмотреть комментарий
    @GetMapping("/post/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("id") Long postId) {
        List<CommentDTO> comment = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comment);
    }
}
