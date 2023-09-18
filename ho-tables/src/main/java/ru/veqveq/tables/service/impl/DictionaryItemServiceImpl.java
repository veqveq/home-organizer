package ru.veqveq.tables.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DictionaryItemServiceImpl implements DictionaryItemService {
    private final DictionaryService dictionaryService;
    private final RestHighLevelClient client;
    private final DictionaryItemMapper mapper;
    private final ElasticsearchService esService;

    @Override
    public UUID saveItem(@Valid SaveDictionaryItemDto dto) {
        log.info("Saving item {} has started", dto.toString());
        try {
            Dictionary dictionary = dictionaryService.getById(dto.getDictionaryId());
            IndexRequest request = new IndexRequest(dictionary.getEsIndexName());
            UUID id = UUID.randomUUID();
            request.source(dto.getFieldValues());
            request.id(id.toString());
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            client.index(request, RequestOptions.DEFAULT);
            log.info("Saving item {} successful", dto.toString());
            return id;
        } catch (IOException e) {
            log.error("Saving item {} failed", dto.toString());
            throw new HoException(String.format("Ошибка при сохранении записи справочника [%s]: %s",
                    dto.toString(), e.getMessage()));
        }
    }

    @Override
    public Page<OutputDictionaryItemDto> filter(UUID dictId, DictionaryItemFilter filter, Pageable pageable) {
        log.info("Elasticsearch filtering by criteria {} has started", filter);
        Dictionary dictionary = dictionaryService.getById(dictId);
        List<String> textFields = ElasticUtils.getTextFields(dictionary, UUID::toString);
        try {
            SearchRequest searchRequest = new SearchRequest(dictionary.getEsIndexName());
            SearchSourceBuilder builder = new SearchSourceBuilder();

            if (Objects.nonNull(filter)) {
                BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
                ElasticUtils.addCommonFilters(queryBuilder, dictionary, filter.getCommonFilter());
                ElasticUtils.addFieldFilters(queryBuilder, dictionary, filter.getFieldFilters());
                builder.query(queryBuilder);
                builder.highlighter(ElasticUtils.addHighlight(dictionary, filter));
            }

            builder.from(pageable.getPageNumber() * pageable.getPageSize());
            builder.size(pageable.getPageSize());
            pageable.getSort().stream()
                    .forEach(order -> {
                        String sortProperty = order.getProperty();
                        if (textFields.contains(sortProperty)) {
                            sortProperty = StringUtils.joinWith(".",sortProperty, ElasticUtils.RAW_INDEX_FIELD_PREFIX);
                        }
                        builder.sort(sortProperty, SortOrder.valueOf(order.getDirection().name()));
                    });

            searchRequest.source(builder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            List<OutputDictionaryItemDto> items = Arrays.stream(response.getHits().getHits())
                    .map(ElasticUtils::getHitValue)
                    .collect(Collectors.toList());
            log.info("Elasticsearch filtering by criteria {} successful. Found {} items", filter, items.size());
            return new PageImpl<>(items, pageable, response.getHits().getTotalHits().value);
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
            IndexRequest request = new IndexRequest(dictionary.getEsIndexName());
            request.source(dto.getFieldValues());
            request.id(dto.getId().toString());
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            client.index(request, RequestOptions.DEFAULT);
            log.info("Updating item {} successful", dto.toString());
            return mapper.toGetDto(dto);
        } catch (IOException e) {
            log.error("Updating item {} failed", dto.toString());
            throw new HoException(String.format("Ошибка при обновлении записи справочника [%s]: %s",
                    dto.toString(), e.getMessage()));
        }
    }

    @Override
    public void delete(UUID dictId, UUID uuid) {
        log.info("Deleting item {} from index {} has started", uuid, dictId);
        try {
            Dictionary dictionary = dictionaryService.getById(dictId);
            DeleteRequest request = new DeleteRequest(dictionary.getEsIndexName());
            request.id(uuid.toString());
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            client.delete(request, RequestOptions.DEFAULT);
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
        DictionaryField field = dictionary.getFields().stream().filter(fld -> fld.getId().equals(fieldId)).findFirst().orElseThrow(() -> new HoNotFoundException(String.format("Поле с id: %s не найдено", fieldId)));
        String fieldName = fieldId.toString();
        if (field.getType() == DictionaryFieldType.Text) {
            fieldName = StringUtils.joinWith(".",fieldName, ElasticUtils.RAW_INDEX_FIELD_PREFIX);
        }
        return esService.isUniqueFieldValue(dictionary.getEsIndexName(), itemId, fieldName, fieldValue);
    }
}
