package ru.veqveq.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.veqveq.backend.dto.item.OutputDictionaryItemDto;
import ru.veqveq.backend.dto.item.input.impl.SaveDictionaryItemDto;
import ru.veqveq.backend.dto.item.input.impl.UpdateDictionaryItemDto;
import ru.veqveq.backend.exception.HOException;
import ru.veqveq.backend.mapper.DictionaryItemMapper;
import ru.veqveq.backend.model.DictionaryItemFilter;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.service.DictionaryItemService;
import ru.veqveq.backend.service.DictionaryService;
import ru.veqveq.backend.util.ElasticUtils;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class DictionaryItemServiceImpl implements DictionaryItemService {
    private final DictionaryService dictionaryService;
    private final RestHighLevelClient client;
    private final DictionaryItemMapper mapper;

    @Override
    public UUID saveItem(@Valid SaveDictionaryItemDto dto) {
        try {
            Dictionary dictionary = dictionaryService.getById(dto.getDictionaryId());
            IndexRequest request = new IndexRequest(dictionary.getEsIndexName());
            UUID id = UUID.randomUUID();
            request.source(dto.getFieldValues());
            request.id(id.toString());
            client.index(request, RequestOptions.DEFAULT);
            return id;
        } catch (IOException e) {
            throw new HOException(e.getMessage());
        }
    }

    @Override
    public Page<OutputDictionaryItemDto> findAll(UUID dictId, Pageable pageable) {
        return filter(dictId, null, pageable);
    }

    @Override
    public Page<OutputDictionaryItemDto> filter(UUID dictId, DictionaryItemFilter filter, Pageable pageable) {
        Dictionary dictionary = dictionaryService.getById(dictId);
        try {
            SearchRequest searchRequest = new SearchRequest(dictionary.getEsIndexName());
            SearchSourceBuilder builder = new SearchSourceBuilder();

            if (Objects.nonNull(filter)) {
                BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
                ElasticUtils.addCommonFilters(queryBuilder, dictionary, filter.getCommonFilters());
                ElasticUtils.addFieldFilters(queryBuilder, filter.getFieldFilters());
                builder.query(queryBuilder);
                builder.highlighter(ElasticUtils.addHighlight(dictionary, filter));
            }

            builder.from(pageable.getPageNumber() * pageable.getPageSize());
            builder.size(pageable.getPageSize());
            pageable.getSort().stream().forEach(order -> builder.sort(order.getProperty(), SortOrder.valueOf(order.getDirection().name())));

            searchRequest.source(builder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            List<OutputDictionaryItemDto> items = Arrays.stream(response.getHits().getHits())
                    .map(hit -> new OutputDictionaryItemDto(UUID.fromString(hit.getId()), hit.getSourceAsMap()))
                    .collect(Collectors.toList());
            return new PageImpl<>(items, pageable, response.getHits().getTotalHits().value);
        } catch (IOException e) {
            throw new HOException("Ошибка поиска Elasticsearch");
        }
    }

    @Override
    public OutputDictionaryItemDto update(@Valid UpdateDictionaryItemDto dto) {
        try {
            Dictionary dictionary = dictionaryService.getById(dto.getDictionaryId());
            UpdateRequest request = new UpdateRequest(dictionary.getEsIndexName(), dto.getId().toString());
            request.doc(dto.getFieldValues());
            client.update(request, RequestOptions.DEFAULT);
            return mapper.toGetDto(dto);
        } catch (IOException e) {
            throw new HOException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID dictId, UUID uuid) {
        try {
            Dictionary dictionary = dictionaryService.getById(dictId);
            DeleteRequest request = new DeleteRequest(dictionary.getEsIndexName());
            request.id(uuid.toString());
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new HOException(e.getMessage());
        }
    }
}
