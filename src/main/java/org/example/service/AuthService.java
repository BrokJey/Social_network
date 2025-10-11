package org.example.service;

import org.example.dto.JwtResponse;
import org.example.dto.LoginDTO;
import org.example.dto.RegisterDTO;

public interface AuthService {
    JwtResponse register(RegisterDTO request);
    JwtResponse login(LoginDTO request);
}
