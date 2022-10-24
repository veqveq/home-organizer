package ru.veqveq.backend.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.util.CollectionUtils;
import ru.veqveq.backend.dto.item.OutputDictionaryItemDto;
import ru.veqveq.backend.model.DictionaryItemFilter;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.model.entity.DictionaryField;
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ElasticUtils {
    public void addCommonFilters(BoolQueryBuilder builder, Dictionary dictionary, String commonFilter) {
        String[] fieldNames = getTextFields(dictionary, UUID::toString).toArray(new String[0]);
        BoolQueryBuilder commonBuilder = new BoolQueryBuilder();
        if (StringUtils.isNotBlank(commonFilter)) {
            Set<String> tokens = Set.of(commonFilter.trim().replaceAll("\\s+", " ").split("\\s"));
            for (String token : tokens) {
                for (String fieldName : fieldNames) {
                    BoolQueryBuilder fieldBuilder = new BoolQueryBuilder();
                    fieldBuilder.must(QueryBuilders.matchPhrasePrefixQuery(fieldName, token));
                    fieldBuilder.must(QueryBuilders.matchPhrasePrefixQuery(fieldName, commonFilter));
                    commonBuilder.should(fieldBuilder);
                }
            }
            builder.must(commonBuilder);
        }
    }

    public void addFieldFilters(BoolQueryBuilder builder, Dictionary dictionary, Map<String, Object> filters) {
        if (!CollectionUtils.isEmpty(filters)) {
            List<String> textFields = getTextFields(dictionary, UUID::toString);
            List<String> notTextFields = getNotTextFields(dictionary, UUID::toString);
            filters.forEach((key, value) -> {
                if (textFields.contains(key)) {
                    builder.must(QueryBuilders.matchPhrasePrefixQuery(key, value));
                } else if (notTextFields.contains(key)) {
                    builder.must(QueryBuilders.matchQuery(key, value));
                }
            });
        }
    }

    public HighlightBuilder addHighlight(Dictionary dictionary, DictionaryItemFilter filter) {
        HighlightBuilder builder = new HighlightBuilder();
        builder.requireFieldMatch(false);
        builder.preTags("<b class= \"text-red-600\">");
        builder.postTags("</b>");
        builder.highlighterType("fvh");
        builder.boundaryScannerType(HighlightBuilder.BoundaryScannerType.CHARS);
        Set<HighlightBuilder.Field> highlightFields = new HashSet<>();
        if (StringUtils.isNotBlank(filter.getCommonFilter())) {
            highlightFields.addAll(getTextFields(dictionary, val -> new HighlightBuilder.Field(val.toString())));
        }
        if (!CollectionUtils.isEmpty(filter.getFieldFilters())) {
            highlightFields.addAll(filter.getFieldFilters().keySet().stream().map(HighlightBuilder.Field::new).collect(Collectors.toList()));
        }
        builder.fields().addAll(highlightFields);
        return builder;
    }

    public OutputDictionaryItemDto getHitValue(SearchHit searchHit) {
        OutputDictionaryItemDto dto = new OutputDictionaryItemDto();
        dto.setId(UUID.fromString(searchHit.getId()));
        dto.setFieldValues(searchHit.getSourceAsMap());
        if (!searchHit.getHighlightFields().isEmpty()) {
            searchHit.getHighlightFields().forEach((k, v) -> dto.getFieldValues().put(k, StringUtils.join(v.fragments())));
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
