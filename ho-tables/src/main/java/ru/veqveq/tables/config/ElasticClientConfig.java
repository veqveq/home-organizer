package ru.veqveq.tables.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.veqveq.tables.config.properties.ElasticProperties;

@Configuration
@EnableConfigurationProperties({ElasticProperties.class})
@RequiredArgsConstructor
public class ElasticClientConfig {
    private final ElasticProperties properties;

    @Bean
    public ElasticsearchClient client(){
        RestClient restClient = RestClient.builder(HttpHost.create(properties.getHost())).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
