package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.RoleDTO;
import org.example.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    //Создание роли
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        RoleDTO created = roleService.createRole(roleDTO);
        return ResponseEntity.ok(created);
    }

    //Удаление роли
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    //Получение роли по ID
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
       return roleService.getRoleById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Поиск по названию
    @GetMapping("/search")
    public ResponseEntity<RoleDTO> getRoleByName(@RequestParam String name) {
        return roleService.getRoleByName(name).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Все роли
    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    //Назначить роль пользователю
    @PostMapping("/assign")
    public ResponseEntity<Optional<RoleDTO>> assignRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        Optional<RoleDTO> assignRole = roleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(assignRole);
    }

    //Удалить роль у пользователя
    @PostMapping("/remove")
    public ResponseEntity<Optional<RoleDTO>> removeRoleFromUser(@RequestParam Long userId, @RequestParam Long roleId) {
        Optional<RoleDTO> removeRole = roleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(removeRole);
    }
}
