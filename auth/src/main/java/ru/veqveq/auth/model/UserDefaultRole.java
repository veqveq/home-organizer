package ru.veqveq.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum UserDefaultRole {
    ROLE_OWNER(List.of(UserPermission.values())),
    ROLE_GUEST(Collections.emptyList());

    private final List<UserPermission> permissions;

    public static List<String> asList() {
        return Stream.of(UserDefaultRole.values()).map(Enum::name).collect(Collectors.toList());
    }

    public List<String> getPermissionNames() {
        return permissions.stream().map(UserPermission::name).collect(Collectors.toList());
    }
}
