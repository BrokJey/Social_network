package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.JwtResponse;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.dto.UserDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.security.JwtTokenProvider;
import org.example.service.AuthService;
import org.example.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponse register(RegisterDTO request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Error: Пользователь с таким логином уже существует");
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));

        UserDTO userDTO = UserDTO.builder()
                .username(request.getUsername())
                .firstName(request.getUsername())
                .build();

        UserDTO savedUser = userService.registerUser(userDTO);

        User user = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("Ошибка при регистрации"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new JwtResponse(token);
    }

    @Override
    public JwtResponse login(LoginDTO request) {
        var auth = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        authenticationManager.authenticate(auth);

        String token = jwtTokenProvider.generateToken(request.getUsername());
        return new JwtResponse(token);
    }
}
