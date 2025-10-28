package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.JwtResponse;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8000")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterDTO registerDTO) {
        JwtResponse jwtResponse = authService.register(registerDTO);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDTO loginDTO) {
        JwtResponse jwtResponse = authService.login(loginDTO);
        return ResponseEntity.ok(jwtResponse);
    }
}
