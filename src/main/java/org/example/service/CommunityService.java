package org.example.service;

import org.example.dto.CommunityDTO;
import org.example.dto.UserDTO;

import java.util.List;

public interface CommunityService {
    CommunityDTO createCommunity(Long adminId, String name, String description);
    void deleteCommunity(Long communityId, Long adminId);
    void joinCommunity(Long communityId, Long userId);
    void leaveCommunity(Long communityId, Long userId);
    List<CommunityDTO> getAllCommunities();
    List<CommunityDTO> getUserCommunities(Long userId);
    List<UserDTO> getCommunityMembers(Long communityId);
    void addPostToCommunity(Long communityId, Long userId, String content);
    CommunityDTO updateCommunity(Long communityId, Long adminId, CommunityDTO updatedDTO);
}