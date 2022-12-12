package ru.veqveq.auth.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.authorization.client.AuthzClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import ru.veqveq.auth.utils.KeycloakClientBuilder;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    @Bean
    public KeycloakSpringBootConfigResolver configResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    public RealmResource keycloakRealm(KeycloakSpringBootProperties props) {
        return KeycloakClientBuilder.initRealm(props);
    }

    @Bean
    public ClientResource keycloakClient(KeycloakSpringBootProperties properties) {
        ClientResource client = KeycloakClientBuilder.initClient(keycloakRealm(properties), properties);
        KeycloakClientBuilder.initPermissions(client);
        return client;
    }

    @Bean
    @DependsOn("keycloakClient")
    public AuthzClient keycloakAuthzClient(KeycloakSpringBootProperties props) {
        org.keycloak.authorization.client.Configuration config = new org.keycloak.authorization.client.Configuration(
                props.getAuthServerUrl(), props.getRealm(),
                props.getResource(), props.getCredentials(), null);
        return AuthzClient.create(config);
    }

    @Bean
    public Keycloak keycloak(KeycloakSpringBootProperties props) {
        return KeycloakBuilder.builder()
                .serverUrl(props.getAuthServerUrl())
                .realm(props.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(props.getResource())
                .clientSecret(props.getCredentials().get("secret").toString())
                .build();
    }
}
