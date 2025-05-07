package org.example.mapper;

import org.example.dto.RoleDTO;
import org.example.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    Role fromDTO(RoleDTO dto);
}
