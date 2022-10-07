package ru.veqveq.backend.config;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import ru.veqveq.backend.config.properties.ElasticProperties;

@Configuration
@EnableConfigurationProperties({ElasticProperties.class})
@RequiredArgsConstructor
public class ElasticClientConfig {
    private final ElasticProperties properties;

    @Bean
    public RestHighLevelClient elasticClient() {
        ClientConfiguration configuration = ClientConfiguration.builder()
                .connectedTo(properties.getHost())
                .withConnectTimeout(30000L)
                .withSocketTimeout(30000L)
                .build();
        return RestClients.create(configuration).rest();
    }
}
