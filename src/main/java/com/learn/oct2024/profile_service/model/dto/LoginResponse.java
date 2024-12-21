package com.learn.oct2024.profile_service.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private boolean successful;

    private String token;

    private String id;

    private String username;

    private String role;

    private Integer balance;
}
