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
import ru.veqveq.backend.exception.HoException;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.model.entity.DictionaryField;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

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
                throw new HoException(String.format("Не удалось создать индекс словаря '%s'", dictionary.getName()));
            }
            log.info("ES index: {} created", dictionary.getEsIndexName());
        } catch (IOException e) {
            log.error("ES index creation error: {}", e.getMessage());
            throw new HoException(String.format("Не удалось создать индекс для словаря '%s'. Message: [%s]",
                    dictionary.getName(), e.getMessage()));
        }
    }

    public void migrateIndex(String sourceIndex, String targetIndex) {
        log.info("Reindex {} -> {} has started", sourceIndex, targetIndex);
        try {
            ReindexRequest reindexRequest = new ReindexRequest();
            reindexRequest.setSourceIndices(sourceIndex);
            reindexRequest.setConflicts("proceed");
            reindexRequest.setDestIndex(targetIndex);
            esClient.reindex(reindexRequest, RequestOptions.DEFAULT);
            log.info("Reindex {} -> {} successful", sourceIndex, targetIndex);
        } catch (IOException e) {
            log.error("Reindex {} -> {} failed", sourceIndex, targetIndex);
            throw new HoException(String.format("Ошибка реиндексации %s -> %s: %s",
                    sourceIndex, targetIndex, e.getMessage()));
        }
    }

    public void deleteIndex(Dictionary dictionary) {
        log.info("Deletion {} has started", dictionary.getEsIndexName());
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(dictionary.getEsIndexName());
            AcknowledgedResponse response = esClient.indices().delete(request, RequestOptions.DEFAULT);
            if (!response.isAcknowledged()) {
                log.error("Index {} is not acknowledged", dictionary.getEsIndexName());
                throw new HoException(String.format("Не удалось удалить индекс словаря '%s'", dictionary.getName()));
            }
        } catch (IOException e) {
            log.error("ES index delete error: {}", e.getMessage());
            throw new HoException(String.format("Не удалось удалить индекс словаря '%s'. Message: [%s]",
                    dictionary.getName(), e.getMessage()));
        }
    }

    public boolean isUniqueFieldValue(@NotBlank String indexName,
                                      @NotNull UUID itemId,
                                      @NotBlank String fieldName,
                                      @NotNull Object fieldValue) {
        log.info("Index: {}. Item: {}. Checking field {}:{} for unique has started",
                indexName, itemId, fieldName, fieldValue);
        try {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                    .must(QueryBuilders.matchPhraseQuery(fieldName, fieldValue));
            boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery("_id", itemId.toString()));
            CountRequest request = new CountRequest(indexName);
            request.query(boolQueryBuilder);
            CountResponse response = esClient.count(request, RequestOptions.DEFAULT);
            return response.getCount() == 0;
        } catch (IOException e) {
            log.info("Checking for unique failed");
            throw new HoException(String.format("Ошибка в процессе проверки уникальности значения [%s:%s]: %s",
                    fieldName, fieldValue, e.getMessage()));
        }
    }

    public boolean isUniqueFieldValue(@NotBlank String indexName,
                                      @NotBlank String fieldName,
                                      @NotNull Object fieldValue) {
        log.info("Index: {}. Checking field {}:{} for unique has started", indexName, fieldName, fieldValue);
        try {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                    .must(QueryBuilders.matchPhraseQuery(fieldName, fieldValue));
            CountRequest request = new CountRequest(indexName);
            request.query(boolQueryBuilder);
            CountResponse response = esClient.count(request, RequestOptions.DEFAULT);
            return response.getCount() == 0;
        } catch (IOException e) {
            log.info("Checking for unique failed");
            throw new HoException(String.format("Ошибка в процессе проверки уникальности значения [%s:%s]: %s",
                    fieldName, fieldValue, e.getMessage()));
        }
    }
}
