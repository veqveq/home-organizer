package ru.veqveq.auth.mapper;

import org.keycloak.representations.AccessTokenResponse;
import org.mapstruct.Mapper;
import ru.veqveq.auth.dto.LoginResponse;
import ru.veqveq.auth.model.User;

@Mapper
public interface CredentialsMapper {
    LoginResponse toDto(AccessTokenResponse source, User user);
    LoginResponse toDto(AccessTokenResponse source);
}
