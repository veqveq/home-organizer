package ru.veqveq.backend.util;

import lombok.experimental.UtilityClass;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.util.CollectionUtils;
import ru.veqveq.backend.model.DictionaryItemFilter;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.model.entity.DictionaryField;
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ElasticUtils {
    public void addCommonFilters(BoolQueryBuilder builder, Dictionary dictionary, List<String> commons) {
        String[] fieldNames = getTextFields(dictionary, UUID::toString).toArray(new String[0]);
        Set<String> tokens = commons.stream()
                .map(common -> {
                    String commonStr = common.trim().replaceAll("\\s+", " ");
                    return Set.of(commonStr.split("\\s"));
                })
                .reduce((strings, strings2) -> {
                    strings.addAll(strings2);
                    return strings;
                })
                .orElse(Collections.emptySet());
        for (String token : tokens) {
            builder.should(QueryBuilders.multiMatchQuery(token, fieldNames).fuzziness(Fuzziness.AUTO));
            for (String fieldName : fieldNames) {
                builder.must(QueryBuilders.matchPhrasePrefixQuery(fieldName, token));
            }
        }
    }

    public void addFieldFilters(BoolQueryBuilder builder, Map<String, Object> filters) {
        if (!CollectionUtils.isEmpty(filters)) {
            filters.forEach((key, value) -> builder.must(QueryBuilders.matchPhrasePrefixQuery(key, value)));
        }
    }

    public HighlightBuilder addHighlight(Dictionary dictionary, DictionaryItemFilter filter) {
        HighlightBuilder builder = new HighlightBuilder();
        Set<HighlightBuilder.Field> highlightFields = new HashSet<>();
        if (!CollectionUtils.isEmpty(filter.getCommonFilters())) {
            highlightFields.addAll(getTextFields(dictionary, val -> new HighlightBuilder.Field(val.toString())));
        }
        if (!CollectionUtils.isEmpty(filter.getFieldFilters())){
            highlightFields.addAll(filter.getFieldFilters().keySet().stream().map(HighlightBuilder.Field::new).collect(Collectors.toList()));
        }
        builder.fields().addAll(highlightFields);
        return builder;
    }


    private <V> List<V> getTextFields(Dictionary dictionary, Function<UUID, V> mappingFunction) {
        return dictionary.getFields()
                .stream()
                .filter(field -> field.getType() == DictionaryFieldType.Text)
                .map(DictionaryField::getId)
                .map(mappingFunction)
                .collect(Collectors.toList());
    }
}
