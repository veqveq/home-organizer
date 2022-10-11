package ru.veqveq.backend.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.backend.dto.item.OutputDictionaryItemDto;
import ru.veqveq.backend.dto.item.input.impl.UpdateDictionaryItemDto;

@Mapper
public interface DictionaryItemMapper {
    OutputDictionaryItemDto toGetDto(UpdateDictionaryItemDto source);
}
