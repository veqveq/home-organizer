package ru.veqveq.backend.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.backend.dto.dictionary.DictionaryFieldDto;
import ru.veqveq.backend.model.entity.DictionaryField;

@Mapper
public interface DictionaryFieldMapper {
    DictionaryFieldDto toDto(DictionaryField entity);
}
