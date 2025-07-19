package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.FriendshipDTO;
import org.example.dto.UserDTO;
import org.example.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    //Отправить запрос в друзья
    @PostMapping("/request/{sender}/{receiver}")
    public ResponseEntity<FriendshipDTO> sendFriendRequest(@PathVariable("sender") Long sender, @PathVariable("receiver") Long receiver) {
        FriendshipDTO request = friendshipService.sendFriendRequest(sender, receiver);
        return ResponseEntity.ok(request);
    }

    //Принятие запроса
    @PostMapping("/accept/{id}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable Long id) {
        friendshipService.acceptFriendRequest(id);
        return ResponseEntity.ok().build();
    }

    //Отмена запроса
    @DeleteMapping("/decline/{id}")
    public ResponseEntity<Void> declineFriendRequest(@PathVariable Long id) {
        friendshipService.declineFriendRequest(id);
        return ResponseEntity.noContent().build();
    }

    //Удалить друга
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        friendshipService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<List<UserDTO>> getFriend(@PathVariable Long id) {
        List<UserDTO> get = friendshipService.getFriends(id);
        return ResponseEntity.ok(get);
    }

    //Посмотреть запросы
    @GetMapping("/pending/{id}")
    public ResponseEntity<List<FriendshipDTO>> getPendingRequests(@PathVariable Long id) {
        List<FriendshipDTO> pending = friendshipService.getPendingRequests(id);
        return ResponseEntity.ok(pending);
    }

}
