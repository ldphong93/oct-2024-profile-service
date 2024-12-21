package com.learn.oct2024.profile_service.service.Impl;

import com.learn.oct2024.common.model.entity.AppUser;
import com.learn.oct2024.profile_service.configuration.security.JwtTokenProvider;
import com.learn.oct2024.profile_service.model.dto.LoginRequest;
import com.learn.oct2024.profile_service.model.dto.LoginResponse;
import com.learn.oct2024.profile_service.repository.UserRepository;
import com.learn.oct2024.profile_service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        try {
            //AuthenticationManager is used to authenticate the user
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));

            /*SecurityContextHolder is used to allows the rest of the application to know
            that the user is authenticated and can use user data from Authentication object */
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Generate the token based on username and secret key
            String token = jwtTokenProvider.generateToken(authentication);

            //Return user info response
            Optional<AppUser> optionalFoundUser = userRepository.findByUsername(loginRequest.getUsername());
            if (optionalFoundUser.isEmpty()) {
                log.info("Can not find App User: " + loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            AppUser foundUser = optionalFoundUser.get();
            LoginResponse response = LoginResponse.builder()
                    .successful(true)
                    .token(token)
                    .id(foundUser.getId())
                    .username(foundUser.getUsername())
                    .role(foundUser.getRole())
                    .balance(foundUser.getBalance())
                    .build();

            log.info("Login successfully with username: {}", foundUser.getUsername());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.info("Authentication fail with: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.info("Something wrong with authentication: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<List<String>> validateToken(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            log.info("Validated token successfully, with token: " + token);
            return ResponseEntity.ok(jwtTokenProvider.getRolesFromToken(token));
        } else {
            log.info("Validated token not successfully, with token as: " + token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
