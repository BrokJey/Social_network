package org.example.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private Long authorId;
    private String authorUsername;
    private Long communityId;
    private LocalDateTime createAt;
}
