package ru.veqveq.auth.utils;

import lombok.experimental.UtilityClass;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import ru.veqveq.auth.model.UserPermission;

import javax.ws.rs.NotFoundException;

@UtilityClass
public class KeycloakClientBuilder {
    public RealmResource initRealm(KeycloakSpringBootProperties props) {
        Keycloak adminClient = getAdminClient(props);
        return getRealm(adminClient, props);
    }

    public ClientResource initClient(RealmResource realm, KeycloakSpringBootProperties props) {
        return realm.clients().findByClientId(props.getResource())
                .stream()
                .filter(client -> client.getClientId().equalsIgnoreCase(props.getResource()))
                .map(clientRepresentation -> realm.clients().get(clientRepresentation.getId()))
                .findFirst()
                .orElseGet(() -> createClient(realm, props));
    }

    public void initPermissions(ClientResource client) {
        UserPermission.asList()
                .stream()
                .filter(permission -> !permissionIsExist(client, permission))
                .forEach(roleName -> {
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setClientRole(true);
                    roleRepresentation.setName(roleName);
                    client.roles().create(roleRepresentation);
                });
    }

    private Keycloak getAdminClient(KeycloakSpringBootProperties props) {
        return KeycloakBuilder.builder()
                .serverUrl(props.getAuthServerUrl())
                .realm("master")
                .username(props.getConfig().get("username").toString())
                .password(props.getConfig().get("password").toString())
                .clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilder()
                        .connectionPoolSize(10).build())
                .build();
    }

    private RealmResource getRealm(Keycloak adminClient, KeycloakSpringBootProperties props) {
        return adminClient.realms().findAll()
                .stream()
                .filter(realm -> realm.getRealm().equalsIgnoreCase(props.getRealm()))
                .map(realm -> adminClient.realm(realm.getRealm()))
                .findFirst()
                .orElseGet(() -> createRealm(adminClient, props));
    }

    private RealmResource createRealm(Keycloak adminClient, KeycloakSpringBootProperties props) {
        RealmRepresentation realm = new RealmRepresentation();
        realm.setEnabled(true);
        realm.setRealm(props.getRealm());
        adminClient.realms().create(realm);
        return adminClient.realm(props.getRealm());
    }

    private ClientResource createClient(RealmResource realm, KeycloakSpringBootProperties props) {
        ClientRepresentation client = new ClientRepresentation();
        client.setStandardFlowEnabled(true);
        client.setClientId(props.getResource());
        client.setAuthorizationServicesEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setServiceAccountsEnabled(true);
        client.setSecret(props.getCredentials().get("secret").toString());
        String clientId = CreatedResponseUtil.getCreatedId(realm.clients().create(client));
        return realm.clients().get(clientId);
    }

    private boolean permissionIsExist(ClientResource clientResource, String permission) {
        try {
            return clientResource.roles()
                    .list()
                    .stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase(permission));
        } catch (NotFoundException e) {
            return false;
        }
    }
}