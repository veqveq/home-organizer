package ru.veqveq.tables.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.tables.dto.dictionary.DictionaryFieldDto;
import ru.veqveq.tables.model.entity.DictionaryField;

@Mapper
public interface DictionaryFieldMapper {
    DictionaryFieldDto toDto(DictionaryField entity);
}
