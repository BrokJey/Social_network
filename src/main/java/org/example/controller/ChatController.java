package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.ChatDTO;
import org.example.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    //Создать чат
    @PostMapping("/create/{user1}/{user2}")
    public ResponseEntity<ChatDTO> createChat(@PathVariable Long user1, @PathVariable Long user2) {
        ChatDTO chat = chatService.createChat(user1, user2);
        return ResponseEntity.ok(chat);
    }

    //Создать групповой чат
    @PostMapping("/group")
    public ResponseEntity<ChatDTO> createGroupChat(@RequestBody Set<Long> participantsIds) {
        ChatDTO chat = chatService.createGroupChat(participantsIds);
        return ResponseEntity.ok(chat);
    }

    //Посмотреть чаты
    @GetMapping("/user/{id}")
    public ResponseEntity<List<ChatDTO>> getUserChats(@PathVariable("id") Long userId) {
        List<ChatDTO> chats = chatService.getUserChats(userId);
        return ResponseEntity.ok(chats);
    }

    //Удалить чат
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long chatId, @RequestParam Long requesterId) {
        chatService.deleteChat(chatId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
