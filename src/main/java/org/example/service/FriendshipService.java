package org.example.service;

import org.example.dto.FriendshipDTO;
import org.example.dto.UserDTO;

import java.util.List;

public interface FriendshipService {
    FriendshipDTO sendFriendRequest(Long senderId, Long receiverId);
    void acceptFriendRequest(Long requestId);
    void declineFriendRequest(Long requestId);
    void removeFriend(Long userId, Long friendId);
    List<UserDTO> getFriends(Long userId);
    List<FriendshipDTO> getPendingRequests(Long userId);
}