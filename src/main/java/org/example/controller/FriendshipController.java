package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.FriendshipDTO;
import org.example.dto.UserDTO;
import org.example.security.CustomUserDetails;
import org.example.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    //Отправить запрос в друзья
    @PostMapping("/request/{receiver}")
    public ResponseEntity<FriendshipDTO> sendFriendRequest(@PathVariable("receiver") Long receiver, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long sender = userDetails.getId();
        
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
    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long friendId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        friendshipService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    //Получить список друзей текущего пользователя
    @GetMapping("/my-friends")
    public ResponseEntity<List<UserDTO>> getMyFriends(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        List<UserDTO> friends = friendshipService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    //Посмотреть входящие запросы в друзья
    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipDTO>> getPendingRequests(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        List<FriendshipDTO> pending = friendshipService.getPendingRequests(userId);
        return ResponseEntity.ok(pending);
    }
}
