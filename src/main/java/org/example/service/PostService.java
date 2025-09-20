package org.example.service;

import org.example.dto.PostDTO;

import java.util.List;
import java.util.Optional;

public interface PostService {
    PostDTO createPostUser(Long userId, PostDTO postDTO);
    PostDTO createPostCommunity(Long communityId, PostDTO postDTO);
    Optional<PostDTO> getPostById(Long postId);
    List<PostDTO> getPostsByUserId(Long userId);
    List<PostDTO> getPostsByCommunityId(Long communityId);
    List<PostDTO> getAllPosts();
    PostDTO updatePost(Long postId, PostDTO updatedPost);
    void deletePost(Long postId);
}
