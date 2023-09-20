package ru.veqveq.tables.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.veqveq.tables.dto.item.OutputDictionaryItemDto;
import ru.veqveq.tables.dto.item.input.impl.SaveDictionaryItemDto;
import ru.veqveq.tables.dto.item.input.impl.UpdateDictionaryItemDto;
import ru.veqveq.tables.exception.HoException;
import ru.veqveq.tables.exception.HoNotFoundException;
import ru.veqveq.tables.mapper.DictionaryItemMapper;
import ru.veqveq.tables.model.DictionaryItemFilter;
import ru.veqveq.tables.model.entity.Dictionary;
import ru.veqveq.tables.model.entity.DictionaryField;
import ru.veqveq.tables.model.enumerated.DictionaryFieldType;
import ru.veqveq.tables.service.DictionaryItemService;
import ru.veqveq.tables.service.DictionaryService;
import ru.veqveq.tables.service.ElasticsearchService;
import ru.veqveq.tables.util.ElasticUtils;

import jakarta.validation.Valid;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DictionaryItemServiceImpl implements DictionaryItemService {
    private final DictionaryService dictionaryService;
    private final ElasticsearchClient client;
    private final DictionaryItemMapper mapper;
    private final ElasticsearchService esService;

    @Override
    public UUID saveItem(@Valid SaveDictionaryItemDto dto) {
        log.info("Saving item {} has started", dto.toString());
        try {
            Dictionary dictionary = dictionaryService.getById(dto.getDictionaryId());
            UUID id = UUID.randomUUID();
            client.index(IndexRequest.of(r -> r
                    .index(dictionary.getEsIndexName())
                    .id(id.toString())
                    .refresh(Refresh.True)
                    .document(dto.getFieldValues())
            ));
            log.info("Saving item {} successful", dto);
            return id;
        } catch (IOException e) {
            log.error("Saving item {} failed", dto);
            throw new HoException(String.format("Ошибка при сохранении записи справочника [%s]: %s",
                    dto, e.getMessage()));
        }
    }

    @Override
    public Page<OutputDictionaryItemDto> filter(UUID dictId, DictionaryItemFilter filter, Pageable pageable) {
        log.info("Elasticsearch filtering by criteria {} has started", filter);
        Dictionary dictionary = dictionaryService.getById(dictId);
        List<String> textFields = ElasticUtils.getTextFields(dictionary, UUID::toString);
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder();

            if (Objects.nonNull(filter)) {
                BoolQuery.Builder queryBuilder = new BoolQuery.Builder();
                ElasticUtils.addCommonFilters(queryBuilder, dictionary, filter.getCommonFilter());
                ElasticUtils.addFieldFilters(queryBuilder, dictionary, filter.getFieldFilters());
                builder.query(queryBuilder.build()._toQuery());
                builder.highlight(ElasticUtils.addHighlight(dictionary, filter));
            }

            builder.from(pageable.getPageNumber() * pageable.getPageSize());
            builder.size(pageable.getPageSize());
            pageable.getSort().stream()
                    .forEach(order -> {
                        String sortProperty = textFields.contains(order.getProperty())
                                ? StringUtils.joinWith(".", order.getProperty(), ElasticUtils.RAW_INDEX_FIELD_PREFIX)
                                : order.getProperty();
                        builder.sort(SortOptions.of(s -> s.field(
                                fld -> fld.field(sortProperty).order(SortOrder.valueOf(StringUtils.capitalize(order.getDirection().name().toLowerCase()))))
                        ));
                    });
            SearchResponse<Map<String, Object>> response = client.search(builder.build(), (Type) Map.class);
            System.out.println(response);
            List<OutputDictionaryItemDto> items = response.hits().hits()
                    .stream()
                    .map(ElasticUtils::getHitValue)
                    .toList();
            log.info("Elasticsearch filtering by criteria {} successful. Found {} items", filter, items.size());
            long total = Objects.nonNull(response.hits().total()) ? response.hits().total().value() : 0;
            return new PageImpl<>(items, pageable, total);
        } catch (IOException e) {
            log.error("Elasticsearch filtering failed: {}", e.getMessage());
            throw new HoException(String.format("Ошибка поиска Elasticsearch: %s", e.getMessage()));
        }
    }

    @Override
    public OutputDictionaryItemDto update(@Valid UpdateDictionaryItemDto dto) {
        log.info("Updating item {} has started", dto.toString());
        try {
            Dictionary dictionary = dictionaryService.getById(dto.getDictionaryId());
            client.index(IndexRequest.of(req -> req
                    .index(dictionary.getEsIndexName())
                    .id(dto.getId().toString())
                    .document(dto.getFieldValues())
                    .refresh(Refresh.True)
            ));
            log.info("Updating item {} successful", dto);
            return mapper.toGetDto(dto);
        } catch (IOException e) {
            log.error("Updating item {} failed", dto);
            throw new HoException(String.format("Ошибка при обновлении записи справочника [%s]: %s",
                    dto, e.getMessage()));
        }
    }

    @Override
    public void delete(UUID dictId, UUID uuid) {
        log.info("Deleting item {} from index {} has started", uuid, dictId);
        try {
            Dictionary dictionary = dictionaryService.getById(dictId);
            client.delete(DeleteRequest.of(req -> req
                    .index(dictionary.getEsIndexName())
                    .id(uuid.toString())
                    .refresh(Refresh.True)
            ));
            log.info("Deleting item {} from index {} successful", uuid, dictId);
        } catch (IOException e) {
            log.info("Deleting item {} from index {} failed", uuid, dictId);
            throw new HoException(String.format("Ошибка при удалении записи [%s] из справочника [%s]: %s",
                    uuid, dictId, e.getMessage()));
        }
    }

    @Override
    public boolean checkUnique(UUID dictionaryId, UUID itemId, UUID fieldId, Object fieldValue) {
        Dictionary dictionary = dictionaryService.getById(dictionaryId);
        DictionaryField field = dictionary.getFields()
                .stream()
                .filter(fld -> fld.getId().equals(fieldId))
                .findFirst()
                .orElseThrow(() -> new HoNotFoundException(String.format("Поле с id: %s не найдено", fieldId)));
        String fieldName = fieldId.toString();
        if (field.getType() == DictionaryFieldType.Text) {
            fieldName = StringUtils.joinWith(".", fieldName, ElasticUtils.RAW_INDEX_FIELD_PREFIX);
        }
        return esService.isUniqueFieldValue(dictionary.getEsIndexName(), itemId, fieldName, fieldValue);
    }
}
