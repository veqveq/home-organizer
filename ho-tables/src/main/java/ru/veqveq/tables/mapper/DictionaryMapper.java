package ru.veqveq.tables.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;
import ru.veqveq.tables.dto.dictionary.DictionaryDto;
import ru.veqveq.tables.model.entity.Dictionary;

import java.util.Objects;
import java.util.UUID;

@Mapper(uses = {DictionaryFieldMapper.class})
public interface DictionaryMapper {
    DictionaryDto toDto(Dictionary entity);

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

    @AfterMapping
    default void sortFields(@MappingTarget DictionaryDto source) {
        source.getFields().sort((o1, o2) -> {
            if (Objects.nonNull(o1.getPosition()) && Objects.nonNull(o2.getPosition())) {
                return o1.getPosition().compareTo(o2.getPosition());
            }
            return 0;
        });
    }
}
