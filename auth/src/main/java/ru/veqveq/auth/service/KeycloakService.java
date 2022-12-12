package ru.veqveq.auth.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.veqveq.auth.exception.HoNotFoundException;
import ru.veqveq.auth.model.UserDefaultRole;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final RealmResource realm;
    private final ClientResource client;

    @PostConstruct
    void init() {
        Arrays.asList(UserDefaultRole.values())
                .forEach(defRole -> createRole(defRole.name(), defRole.getPermissionNames()));
    }

    public void createRole(String name, List<String> permissions) {
        if (client.roles().list().stream().noneMatch(role -> role.getName().equalsIgnoreCase(name))) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(name);
            roleRepresentation.setClientRole(true);
            roleRepresentation.setComposite(true);
            client.roles().create(roleRepresentation);
        }
        RoleResource roleResource = client.roles().get(name);
        roleResource.addComposites(getCompositeRolesList(roleResource, permissions));
    }

    public String createUser(String username, String password, List<String> roles) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setCreatedDate(System.currentTimeMillis());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setCredentials(List.of(credentials));
        user.setEnabled(true);

        UsersResource users = realm.users();
        String userId = CreatedResponseUtil.getCreatedId(users.create(user));
        UserResource userResource = users.get(userId);

        List<RoleRepresentation> clientRoles = client.roles().list()
                .stream()
                .filter(role -> roles.contains(role.getName()))
                .collect(Collectors.toList());
        userResource.roles().clientLevel(client.toRepresentation().getId()).add((clientRoles));
        return userId;
    }

    public UserRepresentation getUserId(String username){
        return realm.users().search(username).stream().findFirst().orElseThrow(()->new HoNotFoundException("Пользователь " + username + "не найден"));
    }

    private List<RoleRepresentation> getCompositeRolesList(RoleResource roleResource, List<String> permissions) {
        List<RoleRepresentation> result = client.roles().list()
                .stream()
                .filter(role -> permissions.contains(role.getName()))
                .collect(Collectors.toList());
        result.removeAll(roleResource.getClientRoleComposites(client.toRepresentation().getId()));
        return result;
    }
}
