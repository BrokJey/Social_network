package org.example.service;

import org.example.dto.RoleDTO;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    RoleDTO createRole(RoleDTO roleDTO);
    void deleteRole(Long id);
    Optional<RoleDTO> getRoleById(Long id);
    Optional<RoleDTO> getRoleByName(String name);
    List<RoleDTO> getAllRoles();
    Optional<RoleDTO> assignRoleToUser(Long userId, Long roleId);
    Optional<RoleDTO> removeRoleFromUser(Long userId, Long roleId);
}