package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.JwtResponse;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return ResponseEntity.ok("Пользователь зарегистрировался");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDTO loginDTO) {
        JwtResponse jwtResponse = authService.login(loginDTO);
        return ResponseEntity.ok(jwtResponse);
    }
}
