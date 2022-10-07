package ru.veqveq.backend.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;
import ru.veqveq.backend.dto.DictionaryDto;
import ru.veqveq.backend.dto.DictionaryMainPageDto;
import ru.veqveq.backend.model.entity.Dictionary;

@Mapper(uses = {DictionaryFieldMapper.class})
public interface DictionaryMapper {
    DictionaryDto toDto(Dictionary entity);

    DictionaryMainPageDto toMainPageDto(Dictionary entity);

    @Mapping(target = "id", ignore = true)
    Dictionary toEntity(DictionaryDto dto);

    @Mapping(target = "id", ignore = true)
    Dictionary toEntity(DictionaryDto dto, String indexName);

    @Mapping(target = "id", ignore = true)
    void merge(@MappingTarget Dictionary target, DictionaryDto source);

    @AfterMapping
    default void setReference(@MappingTarget Dictionary dictionary) {
        if (!CollectionUtils.isEmpty(dictionary.getFields())){
            dictionary.getFields().forEach(field -> field.setDictionary(dictionary));
        }
    }
}
