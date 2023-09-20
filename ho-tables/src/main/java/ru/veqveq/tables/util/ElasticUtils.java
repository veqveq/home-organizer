package ru.veqveq.tables.util;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.JsonData;
import jakarta.json.JsonString;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.util.CollectionUtils;
import ru.veqveq.tables.dto.item.OutputDictionaryItemDto;
import ru.veqveq.tables.model.DictionaryItemFilter;
import ru.veqveq.tables.model.entity.Dictionary;
import ru.veqveq.tables.model.entity.DictionaryField;
import ru.veqveq.tables.model.enumerated.DictionaryFieldType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ElasticUtils {
    public static final String RAW_INDEX_FIELD_PREFIX = "raw";


    public void addCommonFilters(BoolQuery.Builder builder, Dictionary dictionary, String commonFilter) {
        String[] fieldNames = getTextFields(dictionary, UUID::toString).toArray(new String[0]);
        BoolQuery commonQuery = BoolQuery.of(common -> {
            if (StringUtils.isNotBlank(commonFilter)) {
                Set<String> tokens = Set.of(commonFilter.trim().replaceAll("\\s+", " ").split("\\s"));
                for (String fieldName : fieldNames) {
                    BoolQuery.Builder bool = new BoolQuery.Builder();
                    for (String token : tokens) {
                        bool.must(QueryBuilders.matchPhrasePrefix(q -> q.field(fieldName).query(token)));
                    }
                    common.should(bool.build()._toQuery());
                }
            }
            return common;
        });
        builder.must(commonQuery._toQuery());
    }

    public void addFieldFilters(BoolQuery.Builder builder, Dictionary dictionary, Map<String, Object> filters) {
        if (!CollectionUtils.isEmpty(filters)) {
            List<String> textFields = getTextFields(dictionary, UUID::toString);
            List<String> notTextFields = getNotTextFields(dictionary, UUID::toString);
            filters.forEach((key, value) -> {
                if (textFields.contains(key)) {
                    builder.must(QueryBuilders.matchPhrasePrefix(q -> q.field(key).query(value.toString())));
                } else if (notTextFields.contains(key)) {
                    builder.must(QueryBuilders.match(q -> q.field(key).query(FieldValue.of(JsonData.of(value)))));
                }
            });
        }
    }

    public Highlight addHighlight(Dictionary dictionary, DictionaryItemFilter filter) {
        List<Pair<String, HighlightField>> pairs = new ArrayList<>();
        if (StringUtils.isNotBlank(filter.getCommonFilter())) {
            pairs = getTextFields(dictionary, val -> Pair.of(val.toString(), HighlightField.of(fld -> fld.matchedFields(val.toString()))));
        }
        if (!CollectionUtils.isEmpty(filter.getFieldFilters())) {
            pairs.addAll(
                    filter.getFieldFilters()
                            .keySet()
                            .stream()
                            .map(fldName -> Pair.of(fldName, HighlightField.of(fld -> fld.matchedFields(fldName))))
                            .toList());
        }

        Map<String, HighlightField> highlightFields = pairs.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        return Highlight.of(h -> h
                .requireFieldMatch(false)
                .preTags(List.of("<b class= \"text-red-600\">"))
                .postTags(List.of("</b>"))
                .type(HighlighterType.FastVector)
                .boundaryScanner(BoundaryScanner.Chars)
                .fields(highlightFields)
        );
    }

    public OutputDictionaryItemDto getHitValue(Hit<Map<String, Object>> hit) {
        OutputDictionaryItemDto dto = new OutputDictionaryItemDto();
        dto.setId(UUID.fromString(hit.id()));
        dto.setFieldValues(hit.source());
        if (!hit.highlight().isEmpty()) {
            hit.highlight().forEach((k, v) -> dto.getFieldValues().put(k, StringUtils.join(v.toArray())));
        }
        return dto;
    }


    public <V> List<V> getTextFields(Dictionary dictionary, Function<UUID, V> mappingFunction) {
        return dictionary.getFields()
                .stream()
                .filter(field -> field.getType() == DictionaryFieldType.Text)
                .map(DictionaryField::getId)
                .map(mappingFunction)
                .collect(Collectors.toList());
    }

    private <V> List<V> getNotTextFields(Dictionary dictionary, Function<UUID, V> mappingFunction) {
        return dictionary.getFields()
                .stream()
                .filter(field -> field.getType() != DictionaryFieldType.Text)
                .map(DictionaryField::getId)
                .map(mappingFunction)
                .collect(Collectors.toList());
    }
}
