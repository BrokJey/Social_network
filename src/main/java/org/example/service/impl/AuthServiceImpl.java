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

        Role userRole = roleRepository.findByName(org.example.entity.enums.RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));

        // Создаём пользователя сразу с обязательными полями,
        // чтобы пройти Bean Validation (@NotBlank)
        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getUsername())
                .lastName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

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
