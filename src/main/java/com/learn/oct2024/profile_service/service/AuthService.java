package com.learn.oct2024.profile_service.service;

import com.learn.oct2024.profile_service.model.dto.LoginRequest;
import com.learn.oct2024.profile_service.model.dto.LoginResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthService {
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);

    ResponseEntity<List<String>> validateToken(String token);
}
