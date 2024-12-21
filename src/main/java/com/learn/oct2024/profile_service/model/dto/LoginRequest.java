package com.learn.oct2024.profile_service.model.dto;

import lombok.Data;

@Data
public class LoginRequest {

    String username;

    String password;
}
