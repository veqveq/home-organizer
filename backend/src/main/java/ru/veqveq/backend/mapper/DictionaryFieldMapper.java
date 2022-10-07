package ru.veqveq.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.veqveq.backend.dto.DictionaryFieldDto;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.model.entity.DictionaryField;

@Mapper
public interface DictionaryFieldMapper {
    DictionaryFieldDto toDto(DictionaryField entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    DictionaryField toEntity(DictionaryFieldDto dto, Dictionary dictionary);

    @Mapping(target = "id", ignore = true)
    void merge(@MappingTarget DictionaryField target, DictionaryFieldDto source);
}
