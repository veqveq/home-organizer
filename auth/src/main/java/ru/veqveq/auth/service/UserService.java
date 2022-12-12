package ru.veqveq.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veqveq.auth.dto.LoginRequest;
import ru.veqveq.auth.dto.LoginResponse;
import ru.veqveq.auth.dto.UserInfoDto;
import ru.veqveq.auth.exception.HoNotFoundException;
import ru.veqveq.auth.mapper.UserMapper;
import ru.veqveq.auth.model.User;
import ru.veqveq.auth.model.UserDefaultRole;
import ru.veqveq.auth.repo.UserRepository;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostConstruct
    void init() {
//        keycloakService.createUser("owner", "owner", List.of(UserDefaultRole.ROLE_OWNER.name()));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new HoNotFoundException(String.format("Пользователь с email: %s не найден", email)));
    }

    public LoginResponse createUser(@Valid LoginRequest credentials) {
        User user = userMapper.toEntity(credentials);
        String userId = keycloakService.createUser(
                credentials.getEmail(),
                credentials.getPassword(),
                List.of(UserDefaultRole.ROLE_GUEST.name()));
        user.setKcUuid(UUID.fromString(userId));
        userRepository.save(user);
        return authService.login(credentials);
    }

    public boolean checkEmailIsExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkUsernameIsExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public LoginResponse updateUserInfo(UUID userId, UserInfoDto userInfo) {
        return null;
    }
}
