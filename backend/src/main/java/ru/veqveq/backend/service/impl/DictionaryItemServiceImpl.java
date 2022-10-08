package ru.veqveq.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.veqveq.backend.dto.DictionaryItemDto;
import ru.veqveq.backend.exception.HOException;
import ru.veqveq.backend.mapper.DictionaryItemMapper;
import ru.veqveq.backend.model.DictionaryItemFilter;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.service.DictionaryItemService;
import ru.veqveq.backend.service.DictionaryService;
import ru.veqveq.backend.service.ElasticsearchService;
import ru.veqveq.backend.util.ElasticUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryItemServiceImpl implements DictionaryItemService {
    private final DictionaryService dictionaryService;
    private final ElasticsearchService esService;
    private final RestHighLevelClient client;
    private final DictionaryItemMapper mapper;

    @Override
    public UUID saveItem(UUID dictId, DictionaryItemDto dto) {
        try {
            IndexRequest request = new IndexRequest(dictId.toString());
            Dictionary dictionary = dictionaryService.getById(dictId);
            esService.validateUniqueFieldValues(dictionary, dto);

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
    public Page<DictionaryItemDto> findAll(UUID dictId, Pageable pageable) {
        return filter(dictId, null, pageable);
    }

    @Override
    public Page<DictionaryItemDto> filter(UUID dictId, DictionaryItemFilter filter, Pageable pageable) {
        Dictionary dictionary = dictionaryService.getById(dictId);
        try {
            SearchRequest searchRequest = new SearchRequest(dictionary.getId().toString());
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
            List<DictionaryItemDto> items = Arrays.stream(response.getHits().getHits())
                    .map(hit -> {
                        DictionaryItemDto dto = new DictionaryItemDto();
                        dto.setId(hit.getId());
                        dto.setFieldValues(hit.getSourceAsMap());
                        return dto;
                    })
                    .collect(Collectors.toList());
            return new PageImpl<>(items, pageable, response.getHits().getTotalHits().value);
        } catch (IOException e) {
            throw new HOException("Ошибка поиска Elasticsearch");
        }
    }

    @Override
    public DictionaryItemDto update(UUID dictId, UUID uuid, DictionaryItemDto dto) {
        try {
            Dictionary dictionary = dictionaryService.getById(dictId);
            esService.validateUniqueFieldValues(dictionary, uuid, dto);

            IndexRequest request = new IndexRequest(dictId.toString());
            request.source(dto.getFieldValues());
            request.id(uuid.toString());
            client.index(request, RequestOptions.DEFAULT);
            dto.setId(uuid.toString());
            return dto;
        } catch (IOException e) {
            throw new HOException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID dictId, UUID uuid) {
        try {
            DeleteRequest request = new DeleteRequest(dictId.toString());
            request.id(uuid.toString());
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new HOException(e.getMessage());
        }
    }
}
