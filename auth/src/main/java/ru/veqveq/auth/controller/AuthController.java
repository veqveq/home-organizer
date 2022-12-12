package ru.veqveq.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veqveq.auth.dto.LoginRequest;
import ru.veqveq.auth.dto.LoginResponse;
import ru.veqveq.auth.service.AuthService;

@Tag(name = "Контроллер авторизации")
@RestController
@RequestMapping("/ho/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Получить токен пользователя")
    public LoginResponse getToken(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновить токен пользователя")
    public LoginResponse refreshToken(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
