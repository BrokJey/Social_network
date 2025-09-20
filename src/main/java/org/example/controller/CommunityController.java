package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.CommunityDTO;
import org.example.dto.UserDTO;
import org.example.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private  final CommunityService communityService;

    //Создать сообщество
    @PostMapping("/create/{admin}")
    public ResponseEntity<CommunityDTO> createCommunity(@PathVariable("admin") Long adminId, @RequestParam String name, @RequestParam String description) {
        CommunityDTO create = communityService.createCommunity(adminId, name, description);
        return ResponseEntity.ok(create);
    }

    //Удалить сообщество
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable("id") Long communityId, @RequestParam Long adminId) {
        communityService.deleteCommunity(communityId, adminId);
        return ResponseEntity.noContent().build();
    }

    //Присоединиться к сообществу
    @PostMapping("/join")
    public ResponseEntity<Void> joinCommunity(@RequestParam Long communityId, @RequestParam Long userId) {
        communityService.joinCommunity(communityId, userId);
        return ResponseEntity.ok().build();
    }

    //Покинуть сообщество
    @PostMapping("/leave")
    public ResponseEntity<Void> leaveCommunity(@RequestParam Long communityId, @RequestParam Long userId) {
        communityService.leaveCommunity(communityId, userId);
        return ResponseEntity.ok().build();
    }

    //Посмотреть все сообщества
    @GetMapping("/all")
    public ResponseEntity<List<CommunityDTO>> getAllCommunities() {
        List<CommunityDTO> communityes = communityService.getAllCommunities();
        return ResponseEntity.ok(communityes);
    }

    //Посмотреть сообщества пользователя
    @GetMapping("/show/{userId}")
    public ResponseEntity<List<CommunityDTO>> getUserCommunities(@PathVariable Long userId) {
        List<CommunityDTO> communities = communityService.getUserCommunities(userId);
        return ResponseEntity.ok(communities);
    }

    //Посмотреть список участников
    @GetMapping("/members/{communityId}")
    public ResponseEntity<List<UserDTO>> getCommunityMembers(@PathVariable Long communityId) {
        List<UserDTO> members = communityService.getCommunityMembers(communityId);
        return  ResponseEntity.ok(members);
    }

    //Добавить пост
    @PostMapping("/post/{communityId}")
    public ResponseEntity<Void> addPostToCommunity(@PathVariable Long communityId, @RequestParam Long userId, @RequestParam String content) {
        communityService.addPostToCommunity(communityId, userId, content);
        return ResponseEntity.ok().build();
    }

    //Обновить сообщество
    @PostMapping("/update/{communityId}")
    public ResponseEntity<CommunityDTO> updateCommunity(@PathVariable Long communityId, @RequestParam Long adminId, @RequestBody CommunityDTO updatedDTO) {
        CommunityDTO update = communityService.updateCommunity(communityId, adminId, updatedDTO);
        return ResponseEntity.ok(update);
    }
}
