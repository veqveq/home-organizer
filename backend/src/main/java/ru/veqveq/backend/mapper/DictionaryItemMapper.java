package ru.veqveq.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.veqveq.backend.dto.DictionaryItemDto;
import ru.veqveq.backend.model.entity.DictionaryItem;

@Mapper
public interface DictionaryItemMapper {
    @Mapping(target = "id", ignore = true)
    DictionaryItem toEntity(DictionaryItemDto dto);

    DictionaryItemDto toDto(DictionaryItem item);

    @Mapping(target = "id", ignore = true)
    void merge(@MappingTarget DictionaryItem target, DictionaryItemDto source);
}
