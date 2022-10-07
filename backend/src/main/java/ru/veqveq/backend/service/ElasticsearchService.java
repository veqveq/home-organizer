package ru.veqveq.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Service;
import ru.veqveq.backend.exception.HOException;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.model.entity.DictionaryField;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final RestHighLevelClient esClient;

    public void initIndex(Dictionary dictionary) {
        log.info("Start creating ES index: {}", dictionary.getId());
        try {
            CreateIndexRequest request = new CreateIndexRequest(dictionary.getId().toString());

            XContentBuilder builder = XContentFactory.jsonBuilder();

            builder.startObject();
            builder.startObject("properties");
            for (DictionaryField field : dictionary.getFields()) {
                builder.startObject(field.getId().toString());
                builder.field("type", field.getType().name().toLowerCase());
                builder.endObject();
            }
            builder.endObject();
            builder.endObject();

            request.mapping(builder);

            CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);
            if (!response.isAcknowledged()) {
                log.error("Index {} is not acknowledged", dictionary.getId());
                throw new HOException(String.format("Не удалось создать индекс словаря '%s'", dictionary.getName()));
            }
            log.info("ES index: {} created", dictionary.getId());
        } catch (IOException e) {
            log.error("ES index creation error: {}", e.getMessage());
            throw new HOException(String.format("Не удалось создать индекс для словаря '%s'. Message: [%s]",
                    dictionary.getName(), e.getMessage()));
        }
    }

    public void deleteIndex(Dictionary dictionary) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(dictionary.getId().toString());
            AcknowledgedResponse response = esClient.indices().delete(request, RequestOptions.DEFAULT);
            if (!response.isAcknowledged()) {
                log.error("Index {} is not acknowledged", dictionary.getId());
                throw new HOException(String.format("Не удалось удалить индекс словаря '%s'", dictionary.getName()));
            }
        } catch (IOException e) {
            log.error("ES index delete error: {}", e.getMessage());
            throw new HOException(String.format("Не удалось удалить индекс словаря '%s'. Message: [%s]",
                    dictionary.getName(), e.getMessage()));
        }
    }
}
