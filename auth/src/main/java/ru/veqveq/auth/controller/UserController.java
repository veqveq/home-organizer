package ru.veqveq.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.UserInfo;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.auth.dto.LoginRequest;
import ru.veqveq.auth.dto.LoginResponse;
import ru.veqveq.auth.dto.UserInfoDto;
import ru.veqveq.auth.service.UserService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Контроллер для работы с пользователями")
@RestController
@RequestMapping("/ho/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Регистрация нового пользователя")
    public LoginResponse register(@RequestBody LoginRequest credentials) {
        return userService.createUser(credentials);
    }

    @PutMapping("/{user-id}/info")
    @Operation(summary = "Обновить личные данные пользователя")
    public LoginResponse update(
            @PathVariable(name = "user-id") UUID userId,
            @RequestBody UserInfoDto userInfo) {
        return userService.updateUserInfo(userId,userInfo);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить список пользователей")
    @PageableAsQueryParam
    public List<UserInfo> getAll() {
        return null;
    }


    @DeleteMapping
    @Operation(summary = "Удалить пользователя")
    public LoginResponse getToken(@RequestBody UUID userId) {
        return null;
    }
}
