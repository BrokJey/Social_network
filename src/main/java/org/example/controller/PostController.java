package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.PostDTO;
import org.example.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //Создание поста
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestParam Long userId, @RequestBody PostDTO postDTO) {
        PostDTO created = postService.createPost(userId, postDTO);
        return ResponseEntity.ok(created);
    }

    //Поиск поста по id
    @GetMapping("/{id}")
    public  ResponseEntity<PostDTO> getPostsById(@PathVariable Long id) {
        return postService.getPostById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Список постов от пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
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
    public ResponseEntity<PostDTO> updatePost(@PathVariable("id") Long postId, @RequestBody PostDTO updatedDTO) {
        PostDTO updatedPost = postService.updatePost(postId, updatedDTO);
        return ResponseEntity.ok(updatedPost);
    }

    //Удаление поста
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
