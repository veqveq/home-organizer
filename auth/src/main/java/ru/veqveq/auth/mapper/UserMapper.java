package ru.veqveq.auth.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.auth.dto.LoginRequest;
import ru.veqveq.auth.model.User;

@Mapper
public interface UserMapper {
    User toEntity(LoginRequest loginRequest);
}
