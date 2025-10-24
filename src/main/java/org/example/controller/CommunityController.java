package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.CommunityDTO;
import org.example.dto.UserDTO;
import org.example.security.CustomUserDetails;
import org.example.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private  final CommunityService communityService;

    //Создать сообщество
    @PostMapping("/create")
    public ResponseEntity<CommunityDTO> createCommunity(@RequestParam String name, @RequestParam String description, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long adminId = userDetails.getId();
        
        CommunityDTO create = communityService.createCommunity(adminId, name, description);
        return ResponseEntity.ok(create);
    }

    //Удалить сообщество
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable("id") Long communityId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long adminId = userDetails.getId();
        
        communityService.deleteCommunity(communityId, adminId);
        return ResponseEntity.noContent().build();
    }

    //Присоединиться к сообществу
    @PostMapping("/join/{communityId}")
    public ResponseEntity<Void> joinCommunity(@PathVariable Long communityId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        communityService.joinCommunity(communityId, userId);
        return ResponseEntity.ok().build();
    }

    //Покинуть сообщество
    @PostMapping("/leave/{communityId}")
    public ResponseEntity<Void> leaveCommunity(@PathVariable Long communityId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        communityService.leaveCommunity(communityId, userId);
        return ResponseEntity.ok().build();
    }

    //Посмотреть все сообщества
    @GetMapping("/all")
    public ResponseEntity<List<CommunityDTO>> getAllCommunities() {
        List<CommunityDTO> communityes = communityService.getAllCommunities();
        return ResponseEntity.ok(communityes);
    }

    //Посмотреть сообщества текущего пользователя
    @GetMapping("/my-communities")
    public ResponseEntity<List<CommunityDTO>> getMyCommunities(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        List<CommunityDTO> communities = communityService.getUserCommunities(userId);
        return ResponseEntity.ok(communities);
    }

    //Посмотреть список участников
    @GetMapping("/members/{communityId}")
    public ResponseEntity<List<UserDTO>> getCommunityMembers(@PathVariable Long communityId) {
        List<UserDTO> members = communityService.getCommunityMembers(communityId);
        return  ResponseEntity.ok(members);
    }

    //Добавить пост в сообщество
    @PostMapping("/post/{communityId}")
    public ResponseEntity<Void> addPostToCommunity(@PathVariable Long communityId, @RequestParam String content, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        communityService.addPostToCommunity(communityId, userId, content);
        return ResponseEntity.ok().build();
    }

    //Обновить сообщество
    @PostMapping("/update/{communityId}")
    public ResponseEntity<CommunityDTO> updateCommunity(@PathVariable Long communityId, @RequestBody CommunityDTO updatedDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long adminId = userDetails.getId();
        
        CommunityDTO update = communityService.updateCommunity(communityId, adminId, updatedDTO);
        return ResponseEntity.ok(update);
    }
}
