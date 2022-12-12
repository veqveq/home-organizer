package ru.veqveq.auth.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    private UUID userId;
    private String username;
    private String token;
    private String refreshToken;
}
