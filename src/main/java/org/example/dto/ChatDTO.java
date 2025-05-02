package org.example.dto;

import lombok.*;
import org.example.entity.enums.ChatType;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private Long id;
    private ChatType type;
    private Set<Long> participantsIds;
}
