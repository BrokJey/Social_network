package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.MessageDTO;
import org.example.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    //Отправка сообщения
    @PostMapping("/send/{chat}/{user}")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable("chat") Long chatId, @PathVariable("user") Long senderId, @RequestBody MessageDTO messageDTO) {
        MessageDTO send = messageService.sendMessage(chatId, senderId, messageDTO);
        return ResponseEntity.ok(send);
    }

    //Получить сообщение
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Сообщение между пользователями
    @GetMapping("/between/{id1}/{id2}")
    public ResponseEntity<List<MessageDTO>> getMessageBetweenUsers(@PathVariable("id1") Long userId1, @PathVariable("id2") Long userId2) {
        List<MessageDTO> messageDTOS = messageService.getMessageBetweenUsers(userId1, userId2);
        return ResponseEntity.ok(messageDTOS);
    }

    //Все сообщение пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesForUser(@PathVariable("userId") Long id) {
        List<MessageDTO> messageDTOS = messageService.getAllMessagesForUser(id);
        return ResponseEntity.ok(messageDTOS);
    }

    //Удалить сообщение
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
