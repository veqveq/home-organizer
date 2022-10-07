package ru.veqveq.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import ru.veqveq.backend.dto.DictionaryItemDto;
import ru.veqveq.backend.exception.HOException;
import ru.veqveq.backend.mapper.DictionaryItemMapper;
import ru.veqveq.backend.model.constants.ElasticConstants;
import ru.veqveq.backend.model.entity.DictionaryItem;
import ru.veqveq.backend.repo.DictionaryItemRepo;
import ru.veqveq.backend.service.DictionaryItemService;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryItemServiceImpl implements DictionaryItemService {
    private final ElasticsearchOperations operations;
    private final RestHighLevelClient client;
    private final DictionaryItemRepo repo;
    private final DictionaryItemMapper mapper;

    @Override
    public UUID saveItem(DictionaryItemDto dto) {
        DictionaryItem entity = mapper.toEntity(dto);
        entity.setId(UUID.randomUUID().toString());
        return UUID.fromString(checkedSave(entity).getId());
    }

    @Override
    public Page<DictionaryItemDto> findAll(Pageable pageable) {
        return repo.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    public Page<DictionaryItemDto> filter(String phrase, Pageable pageable) {
        phrase = phrase.trim().replaceAll("\\s+", " ");
        List<String> tokens = List.of(phrase.split("\\s"));

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        tokens.forEach(token -> boolQueryBuilder
                .should(QueryBuilders.multiMatchQuery(token, DictionaryItem.Fields.item, DictionaryItem.Fields.description).fuzziness(Fuzziness.AUTO))
                .should(QueryBuilders.matchPhrasePrefixQuery(DictionaryItem.Fields.item, token))
                .should(QueryBuilders.matchPhrasePrefixQuery(DictionaryItem.Fields.description, token)));
        HighlightBuilder builder = new HighlightBuilder()
                .field(DictionaryItem.Fields.item)
                .field(DictionaryItem.Fields.description);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageable)
                .withHighlightBuilder(builder)
                .withPageable(pageable)
                .build();

        SearchHits<DictionaryItem> hits = operations.search(
                query,
                DictionaryItem.class,
                IndexCoordinates.of(ElasticConstants.DICTIONARY_INDEX_NAME)
        );
        List<DictionaryItemDto> items = hits.stream()
                .map(this::setHighlight)
                .collect(Collectors.toList());
        return new PageImpl<>(items,pageable,hits.getTotalHits());
    }

    @Override
    public DictionaryItemDto update(UUID uuid, DictionaryItemDto dto) {
        if (Objects.isNull(uuid)) {
            throw new HOException("Не получен идентификатор записи");
        }
        DictionaryItem item = repo.findById(uuid.toString())
                .orElseThrow(() -> new HOException(String.format("Запись с идентификатором %s не найдена", uuid)));
        mapper.merge(item, dto);
        return mapper.toDto(checkedSave(item));
    }

    @Override
    public void delete(UUID uuid) {
        if (Objects.isNull(uuid)) {
            throw new HOException("Не получен идентификатор записи");
        }
        if (repo.existsById(uuid.toString())) {
            repo.deleteById(uuid.toString());
        } else {
            throw new HOException(String.format("Запись с идентификатором %s не найдена", uuid));
        }
    }

    private DictionaryItem checkedSave(DictionaryItem item) {
        repo.findByItem(item.getItem()).ifPresent(exist -> {
            if (!exist.getId().equals(item.getId())) {
                throw new HOException("Запись " + exist.getItem() + " уже существует");
            }
        });
        return repo.save(item);
    }

    private DictionaryItemDto setHighlight(SearchHit<DictionaryItem> hit) {
        DictionaryItemDto dto = mapper.toDto(hit.getContent());
        hit.getHighlightFields().forEach((key, value) -> {
            try {
                Field dtoField = dto.getClass().getDeclaredField(key);
                dtoField.setAccessible(true);
                dtoField.set(dto, value.get(0));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        });
        return dto;
    }
}
