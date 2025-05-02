package org.example.dto;

import lombok.*;
import org.example.entity.enums.FriendshipStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDTO {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
}