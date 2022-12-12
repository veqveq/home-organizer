package ru.veqveq.auth.dto;

import lombok.Data;
import ru.veqveq.auth.validation.credentials.CredentialsValid;

@Data
@CredentialsValid
public class LoginRequest {
    private String username;
    private String email;
    private String password;
}
