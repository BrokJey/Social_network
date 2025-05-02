package org.example.dto;

import lombok.*;
import org.example.entity.enums.RoleType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Long id;
    private RoleType name;
}
