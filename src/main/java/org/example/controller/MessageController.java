package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.MessageDTO;
import org.example.security.CustomUserDetails;
import org.example.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    //Отправка сообщения
    @PostMapping("/send/{chat}")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable("chat") Long chatId, @RequestBody MessageDTO messageDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long senderId = userDetails.getId();
        
        MessageDTO send = messageService.sendMessage(chatId, senderId, messageDTO);
        return ResponseEntity.ok(send);
    }

    //Получить сообщение
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Сообщение между текущим пользователем и другим
    @GetMapping("/between/{otherUserId}")
    public ResponseEntity<List<MessageDTO>> getMessageBetweenUsers(@PathVariable("otherUserId") Long otherUserId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getId();
        
        List<MessageDTO> messageDTOS = messageService.getMessageBetweenUsers(currentUserId, otherUserId);
        return ResponseEntity.ok(messageDTOS);
    }

    //Все сообщения текущего пользователя
    @GetMapping("/my-messages")
    public ResponseEntity<List<MessageDTO>> getMyMessages(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        List<MessageDTO> messageDTOS = messageService.getAllMessagesForUser(userId);
        return ResponseEntity.ok(messageDTOS);
    }

    //Удалить сообщение
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
