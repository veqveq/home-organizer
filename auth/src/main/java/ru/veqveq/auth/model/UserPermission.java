package ru.veqveq.auth.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum UserPermission {
    PERMISSION_CREATE_USER("Создание учетной записи пользователя"),
    PERMISSION_DELETE_USER("Удаление учетной записи пользователя"),
    PERMISSION_UPDATE_USER("Изменение учетной записи пользователя"),
    PERMISSION_GET_USER_LIST("Получение списка учетных записи пользователей");

    private final String title;

    public static List<String> asList() {
        return Arrays.stream(UserPermission.values())
                .map(UserPermission::name)
                .collect(Collectors.toList());
    }
}
