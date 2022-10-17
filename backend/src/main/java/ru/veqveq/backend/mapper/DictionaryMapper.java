package ru.veqveq.backend.mapper;

import org.mapstruct.*;
import org.springframework.util.CollectionUtils;
import ru.veqveq.backend.dto.dictionary.DictionaryDto;
import ru.veqveq.backend.dto.dictionary.DictionaryMainPageDto;
import ru.veqveq.backend.model.entity.Dictionary;

import java.util.Objects;
import java.util.UUID;

@Mapper(uses = {DictionaryFieldMapper.class})
public interface DictionaryMapper {
    DictionaryDto toDto(Dictionary entity);

    DictionaryMainPageDto toMainPageDto(Dictionary entity);

    @Mapping(target = "id", ignore = true)
    Dictionary toEntity(DictionaryDto dto, UUID esIndexName);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "esIndexName", ignore = true)
    void merge(@MappingTarget Dictionary target, DictionaryDto source);

    @AfterMapping
    default void setReference(@MappingTarget Dictionary dictionary) {
        if (!CollectionUtils.isEmpty(dictionary.getFields())) {
            dictionary.getFields().forEach(field -> field.setDictionary(dictionary));
        }
    }

    @BeforeMapping
    default void sortFields(Dictionary source) {
        source.getFields().sort((o1, o2) -> {
            if (Objects.nonNull(o1.getCreateStamp()) && Objects.nonNull(o2.getCreateStamp())) {
                return o2.getCreateStamp().compareTo(o1.getCreateStamp());
            }
            return 0;
        });
    }
}
