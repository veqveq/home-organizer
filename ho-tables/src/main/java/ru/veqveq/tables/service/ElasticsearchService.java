package ru.veqveq.tables.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch._types.analysis.Analyzer;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.reindex.Destination;
import co.elastic.clients.elasticsearch.core.reindex.Source;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veqveq.tables.exception.HoException;
import ru.veqveq.tables.model.entity.Dictionary;
import ru.veqveq.tables.model.entity.DictionaryField;
import ru.veqveq.tables.model.enumerated.DictionaryFieldType;
import ru.veqveq.tables.util.ElasticUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.*;

import static co.elastic.clients.elasticsearch._types.analysis.TokenChar.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private final ElasticsearchClient esClient;

    public void initIndex(Dictionary dictionary) {
        initIndex(dictionary, null);
    }

    public void initIndex(Dictionary dictionary, UUID indexName) {
        log.info("Start creating ES index: {}", dictionary.getEsIndexName());
        try {
            CreateIndexRequest request = CreateIndexRequest.of(builder -> {
                if (Objects.isNull(indexName)) {
                    builder.index(dictionary.getEsIndexName());
                }else{
                    builder.index(indexName.toString());
                }
                Map<String, Property> properties = new HashMap<>();
                for (DictionaryField field: dictionary.getFields()){
                    Property property = null;
                    if (field.getType() == DictionaryFieldType.Text){
                        property = TextProperty.of(p -> p
                                .termVector(TermVectorOption.WithPositionsOffsets)
                                .analyzer("index_ngram_analyzer")
                                .searchAnalyzer("search_term_analyzer")
                                .fields(ElasticUtils.RAW_INDEX_FIELD_PREFIX, KeywordProperty.of(b->b)._toProperty())
                        )._toProperty();
                    } else if (field.getType() == DictionaryFieldType.Boolean) {
                        property = BooleanProperty.of(builder1 -> builder1)._toProperty();
                    } else if (field.getType() == DictionaryFieldType.Date) {
                        property = DateProperty.of(b->b)._toProperty();
                    } else if (field.getType() == DictionaryFieldType.Long) {
                        property = LongNumberProperty.of(b->b)._toProperty();
                    } else if (field.getType() == DictionaryFieldType.Double) {
                        property = DoubleNumberProperty.of(b->b)._toProperty();
                    }
                    properties.put(field.getId().toString(), property);
                }
                builder.mappings(map -> map.properties(properties));
                builder.settings(generateSettings());
                return builder;
            });

            CreateIndexResponse response = esClient.indices().create(request);
            if (!response.acknowledged()) {
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

    private IndexSettings generateSettings() {
        Analyzer ngramAnalyzer = Analyzer.of(analyzer -> analyzer.custom(config -> config
                .tokenizer("ngram_tokenizer")
                .filter("lowercase")
        ));
        Analyzer searchTermAnalyzer = Analyzer.of(analyzer -> analyzer.custom(config -> config
                .tokenizer("keyword")
                .filter("lowercase")
        ));
        return IndexSettings.of(builder -> builder
                .maxNgramDiff(15)
                .analysis(analysis -> analysis
                        .tokenizer("ngram_tokenizer", tokenizer -> tokenizer.definition(definition ->
                                definition.ngram(ngram -> ngram
                                        .minGram(1)
                                        .maxGram(15)
                                        .tokenChars(List.of(Letter, Digit, Punctuation, Symbol, Whitespace))))
                        ).analyzer(Map.of(
                                        "index_ngram_analyzer", ngramAnalyzer,
                                        "search_term_analyzer", searchTermAnalyzer
                                )
                        )

                )
        );
    }

    public void migrateIndex(String sourceIndex, String targetIndex) {
        log.info("Reindex {} -> {} has started", sourceIndex, targetIndex);
        try {
            esClient.reindex(request -> request
                    .source(Source.of(s -> s.index(sourceIndex)))
                    .conflicts(Conflicts.Proceed)
                    .dest(Destination.of(d -> d.index(targetIndex)))
            );
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
            DeleteIndexResponse response = esClient.indices().delete(
                    request -> request.index(dictionary.getEsIndexName())
            );
            if (!response.acknowledged()) {
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
            BoolQuery boolQuery = BoolQuery.of(builder -> {
                builder.must(query -> query.match(
                        match -> match.field(fieldName).query(val -> val.anyValue(JsonData.of(fieldValue)))));
                if (Objects.nonNull(itemId)) {
                    builder.mustNot(query -> query
                            .matchPhrase(phrase -> phrase
                                    .field("_id")
                                    .query(itemId.toString())
                            )
                    );
                }
                return builder;
            });
            CountResponse response = esClient.count(CountRequest.of(req -> req.index(indexName).query(boolQuery._toQuery())));
            return response.count() == 0;
        } catch (IOException e) {
            log.debug("Checking for unique failed");
            throw new HoException(String.format("Ошибка в процессе проверки уникальности значения [%s:%s]: %s",
                    fieldName, fieldValue, e.getMessage()));
        }
    }
}
