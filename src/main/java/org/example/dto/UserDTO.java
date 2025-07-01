package org.example.dto;

import lombok.*;
import org.example.entity.enums.Gender;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private Set<String> roles;
    private LocalDateTime createdAt;
}