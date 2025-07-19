package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //Создание пользователя (регистрация)
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO created = userService.registerUser(userDTO);
        return ResponseEntity.ok(created);
    }

    //Получение пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    //Обновление пользователя
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, UserDTO updatedDTO) {
        UserDTO updatedUser = userService.updateUser(id, updatedDTO);
        return ResponseEntity.ok(updatedUser);
    }

    //Поиск по имени
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsersByName(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String gender)
    {
        List<UserDTO> users = userService.searchUsers(firstName, lastName, age, gender);
        return ResponseEntity.ok(users);
    }
}
