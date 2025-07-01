package org.example.service;

import org.example.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    CommentDTO addComment(CommentDTO commentDTO);
    void deleteComment(Long commentId, Long postId);
    List<CommentDTO> getCommentsByPostId(Long postId);
}