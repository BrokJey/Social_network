package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ChatDTO;
import org.example.security.CustomUserDetails;
import org.example.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    //Создать чат
    @PostMapping("/create/{user2}")
    public ResponseEntity<ChatDTO> createChat(@PathVariable Long user2, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId1 = userDetails.getId();
        
        ChatDTO chat = chatService.createChat(userId1, user2);
        return ResponseEntity.ok(chat);
    }

    //Создать групповой чат
    @PostMapping("/group")
    public ResponseEntity<ChatDTO> createGroupChat(@RequestBody Set<Long> participantsIds, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getId();
        
        // Добавляем текущего пользователя к участникам
        participantsIds.add(currentUserId);
        
        ChatDTO chat = chatService.createGroupChat(participantsIds);
        return ResponseEntity.ok(chat);
    }

    //Посмотреть чаты текущего пользователя
    @GetMapping("/my-chats")
    public ResponseEntity<List<ChatDTO>> getMyChats(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        List<ChatDTO> chats = chatService.getUserChats(userId);
        return ResponseEntity.ok(chats);
    }

    //Удалить чат
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long chatId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long requesterId = userDetails.getId();
        
        chatService.deleteChat(chatId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
