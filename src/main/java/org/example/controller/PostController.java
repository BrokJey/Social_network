package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.PostDTO;
import org.example.security.CustomUserDetails;
import org.example.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //Создание поста текущим пользователем
    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPostUser(@RequestBody PostDTO postDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        PostDTO created = postService.createPostUser(userId, postDTO);
        return ResponseEntity.ok(created);
    }

    //Создание поста в сообществе
    @PostMapping("/community/{communityId}")
    public ResponseEntity<PostDTO> createPostCommunity(@PathVariable Long communityId, @RequestBody PostDTO postDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        // Устанавливаем автора поста
        postDTO.setAuthorId(userId);
        
        PostDTO created = postService.createPostCommunity(communityId, postDTO);
        return ResponseEntity.ok(created);
    }

    //Поиск поста по id
    @GetMapping("/{id}")
    public  ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return postService.getPostById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Список постов текущего пользователя
    @GetMapping("/my-posts")
    public ResponseEntity<List<PostDTO>> getMyPosts(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        List<PostDTO> userPosts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(userPosts);
    }

    //Список постов от сообщества
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<PostDTO>> getPostsByCommunityId(@PathVariable Long communityId) {
        List<PostDTO> communityPosts = postService.getPostsByCommunityId(communityId);
        return ResponseEntity.ok(communityPosts);
    }

    //Посмотреть все посты
    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    //Обновить пост
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable("id") Long postId, @RequestBody PostDTO updatedDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        // Проверяем, что пользователь может редактировать этот пост
        // (добавить проверку в сервис)
        PostDTO updatedPost = postService.updatePost(postId, updatedDTO);
        return ResponseEntity.ok(updatedPost);
    }

    //Удаление поста
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        // Проверяем, что пользователь может удалить этот пост
        // (добавить проверку в сервис)
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
