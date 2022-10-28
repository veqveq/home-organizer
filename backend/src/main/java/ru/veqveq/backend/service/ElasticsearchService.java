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
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;
import ru.veqveq.backend.util.ElasticUtils;

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
                if (field.getType() == DictionaryFieldType.Text) {
                    builder.field("term_vector", "with_positions_offsets");
                    builder.field("analyzer", "index_ngram_analyzer");
                    builder.field("search_analyzer", "search_term_analyzer");
                    builder.startObject("fields");
                    builder.startObject(ElasticUtils.RAW_INDEX_FIELD_PREFIX);
                    builder.field("type", "keyword");
                    builder.endObject();
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
            builder.endObject();

            request.mapping(builder);
            request.settings(generateSettings());

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

    private XContentBuilder generateSettings() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        return builder.startObject()
                .startObject("index")
                .field("max_ngram_diff", 15)
                .endObject()
                .startObject("analysis")
                .startObject("tokenizer")
                .startObject("ngram_tokenizer")
                .field("type", "nGram")
                .field("min_gram", 1)
                .field("max_gram", 15)
                .field("token_chars", new String[]{"letter", "digit", "punctuation", "symbol", "whitespace"})
                .endObject()
                .endObject()
                .startObject("analyzer")
                .startObject("index_ngram_analyzer")
                .field("type", "custom")
                .field("tokenizer", "ngram_tokenizer")
                .field("filter", new String[]{"lowercase"})
                .endObject()
                .startObject("search_term_analyzer")
                .field("type", "custom")
                .field("tokenizer", "keyword")
                .field("filter", "lowercase")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
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
            log.info("ES index: {} deleted", dictionary.getEsIndexName());
        } catch (IOException e) {
            log.error("ES index delete error: {}", e.getMessage());
            throw new HoException(String.format("Не удалось удалить индекс словаря '%s'. Message: [%s]",
                    dictionary.getName(), e.getMessage()));
        }
    }

    public boolean isUniqueFieldValue(@NotBlank String indexName,
                                      UUID itemId,
                                      @NotBlank String fieldName,
                                      @NotNull Object fieldValue) {
        log.debug("Index: {}. Item: {}. Checking field {}:{} for unique has started",
                indexName, itemId, fieldName, fieldValue);
        try {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                    .must(QueryBuilders.matchQuery(fieldName, fieldValue));
            if (Objects.nonNull(itemId)){
                boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery("_id", itemId.toString()));
            }
            CountRequest request = new CountRequest(indexName);
            request.query(boolQueryBuilder);
            CountResponse response = esClient.count(request, RequestOptions.DEFAULT);
            return response.getCount() == 0;
        } catch (IOException e) {
            log.debug("Checking for unique failed");
            throw new HoException(String.format("Ошибка в процессе проверки уникальности значения [%s:%s]: %s",
                    fieldName, fieldValue, e.getMessage()));
        }
    }
}
