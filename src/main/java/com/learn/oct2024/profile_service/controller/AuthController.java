package com.learn.oct2024.profile_service.controller;

import com.learn.oct2024.profile_service.model.dto.LoginRequest;
import com.learn.oct2024.profile_service.model.dto.LoginResponse;
import com.learn.oct2024.profile_service.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j(topic = "AuthController")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Receive login request with username: {}", request.getUsername());
        return authService.login(request);
    }

    /*
    Check if token valid
    Return list of token's roles, and success code 200
     */
    @PostMapping("/validateToken")
    ResponseEntity<List<String>> validateToken(@RequestBody String token) {
        log.info("Receive token validation request, with token: " + token);
        return authService.validateToken(token);
    }
}
