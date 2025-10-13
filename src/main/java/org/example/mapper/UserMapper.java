package org.example.mapper;

import org.example.dto.UserDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.entity.enums.RoleType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    UserDTO toDTO(User user);

    default Set<String> mapRoles(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    @Mapping(target = "roles", expression = "java(mapRoleNames(dto.getRoles()))")
    User fromDTO(UserDTO dto);

    default Set<Role> mapRoleNames(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return roleNames.stream()
                .map(name -> new Role(null, RoleType.valueOf(name)))
                .collect(Collectors.toSet());
    }
}
