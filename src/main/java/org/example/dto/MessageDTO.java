package org.example.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String content;
    private Long chatId;
    private Long senderId;
    private LocalDateTime sentAt;
}