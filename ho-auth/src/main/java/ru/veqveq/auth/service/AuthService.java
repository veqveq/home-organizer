package ru.veqveq.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.veqveq.auth.dto.LoginRequest;
import ru.veqveq.auth.dto.LoginResponse;
import ru.veqveq.auth.exception.HoAuthException;
import ru.veqveq.auth.mapper.CredentialsMapper;
import ru.veqveq.auth.model.User;
import ru.veqveq.auth.repo.UserRepository;
import ru.veqveq.auth.utils.TokenUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthzClient authzClient;
    private final CredentialsMapper credentialsMapper;
    private final WebClient webClient;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            AccessTokenResponse response = authzClient
                    .authorization(loginRequest.getEmail(), loginRequest.getPassword())
                    .authorize();
            UUID userSub = UUID.fromString(TokenUtils.getFieldFromToken(response.getToken(), "sub").toString());
            User user = userRepository.findById(userSub)
                    .orElseThrow(() -> new HoAuthException(
                            String.format("Не найдена информация о пользователе: %s", loginRequest.getEmail())));
            return credentialsMapper.toDto(response, user);
        } catch (HttpResponseException exception) {
            throw new HoAuthException("Ошибка авторизации! Проверьте email/пароль");
        }
    }

    public LoginResponse refresh(String refreshToken) {
        AccessTokenResponse response = webClient.post()
                .body(BodyInserters.fromFormData(prepareFormData(refreshToken)))
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .doOnError(throwable -> {
                    throw new HoAuthException("Ошибка обновления токена. Выполните повторную авторизацию");
                })
                .block();
        return credentialsMapper.toDto(response);
    }

    private MultiValueMap<String, String> prepareFormData(String refreshToken) {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        result.add("grant_type", "refresh_token");
        result.add("refresh_token", refreshToken);
        result.add("client_id", authzClient.getConfiguration().getResource());
        result.add("client_secret", authzClient.getConfiguration().getCredentials().get("secret").toString());
        return result;
    }
}
