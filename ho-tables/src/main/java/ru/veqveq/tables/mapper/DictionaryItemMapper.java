package ru.veqveq.tables.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.tables.dto.item.OutputDictionaryItemDto;
import ru.veqveq.tables.dto.item.input.impl.UpdateDictionaryItemDto;

@Mapper
public interface DictionaryItemMapper {
    OutputDictionaryItemDto toGetDto(UpdateDictionaryItemDto source);
}
