package ru.veqveq.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.stereotype.Service;
import ru.veqveq.backend.dto.DictionaryItemDto;
import ru.veqveq.backend.exception.HOException;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.model.entity.DictionaryField;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final RestHighLevelClient esClient;

    public void initIndex(Dictionary dictionary) {
        initIndex(dictionary, null);
    }

    public void initIndex(Dictionary dictionary, UUID indexName) {
        log.info("Start creating ES index: {}", dictionary.getEsIndexName());
        try {
            CreateIndexRequest request = Objects.isNull(indexName)
                    ? new CreateIndexRequest(dictionary.getEsIndexName())
                    : new CreateIndexRequest(indexName.toString());

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
                log.error("Index {} is not acknowledged", dictionary.getEsIndexName());
                throw new HOException(String.format("Не удалось создать индекс словаря '%s'", dictionary.getName()));
            }
            log.info("ES index: {} created", dictionary.getEsIndexName());
        } catch (IOException e) {
            log.error("ES index creation error: {}", e.getMessage());
            throw new HOException(String.format("Не удалось создать индекс для словаря '%s'. Message: [%s]",
                    dictionary.getName(), e.getMessage()));
        }
    }

    public void migrateIndex(String sourceIndex, String targetIndex) {
        try {
            ReindexRequest reindexRequest = new ReindexRequest();
            reindexRequest.setSourceIndices(sourceIndex);
            reindexRequest.setConflicts("proceed");
            reindexRequest.setDestIndex(targetIndex);
            esClient.reindex(reindexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new HOException(e.getMessage());
        }
    }

    public void deleteIndex(Dictionary dictionary) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(dictionary.getEsIndexName());
            AcknowledgedResponse response = esClient.indices().delete(request, RequestOptions.DEFAULT);
            if (!response.isAcknowledged()) {
                log.error("Index {} is not acknowledged", dictionary.getEsIndexName());
                throw new HOException(String.format("Не удалось удалить индекс словаря '%s'", dictionary.getName()));
            }
        } catch (IOException e) {
            log.error("ES index delete error: {}", e.getMessage());
            throw new HOException(String.format("Не удалось удалить индекс словаря '%s'. Message: [%s]",
                    dictionary.getName(), e.getMessage()));
        }
    }

    public void validateUniqueFieldValues(Dictionary dictionary, UUID itemId, DictionaryItemDto itemDto) {
        StringBuilder errorMessage = new StringBuilder();
        Set<DictionaryField> uniqueFieldNames = dictionary.getFields()
                .stream()
                .filter(DictionaryField::getUniqueValue)
                .collect(Collectors.toSet());
        for (DictionaryField field : uniqueFieldNames) {
            String fieldName = field.getId().toString();
            Object fieldValue = itemDto.getFieldValues().get(fieldName);
            if (Objects.nonNull(fieldValue) && !isUniqueFieldValue(dictionary.getEsIndexName(), itemId, fieldName, fieldValue)) {
                errorMessage.append(String.format("[%s:%s];%n", field.getName(), fieldValue));
            }
        }
        if (errorMessage.length() != 0) {
            throw new HOException("Ошибка заполнения уникальных полей: \n" + errorMessage.toString());
        }
    }


    public void validateUniqueFieldValues(Dictionary dictionary, DictionaryItemDto itemDto) {
        validateUniqueFieldValues(dictionary, null, itemDto);
    }

    private boolean isUniqueFieldValue(String indexName, UUID itemId, String fieldName, Object fieldValue) {
        try {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                    .must(QueryBuilders.matchPhraseQuery(fieldName, fieldValue));
            if (Objects.nonNull(itemId)) {
                boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery("_id", itemId.toString()));
            }
            CountRequest request = new CountRequest(indexName);
            request.query(boolQueryBuilder);
            CountResponse response = esClient.count(request, RequestOptions.DEFAULT);
            return response.getCount() == 0;
        } catch (IOException e) {
            throw new HOException(e.getMessage());
        }
    }
}
