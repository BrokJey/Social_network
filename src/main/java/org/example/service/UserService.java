package org.example.service;

import org.example.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO updatedDTO);
    Optional<UserDTO> getUserById(Long id);
    List<UserDTO> searchUsers(String firstName, String lastName, Integer age, String gender);
}