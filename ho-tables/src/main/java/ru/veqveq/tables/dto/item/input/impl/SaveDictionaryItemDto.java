package ru.veqveq.tables.dto.item.input.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.veqveq.tables.dto.item.input.InputDictionaryItemDto;
import ru.veqveq.tables.validation.item.DictionaryItemValid;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "Сохранение новой записи")
@ToString
@DictionaryItemValid
public class SaveDictionaryItemDto extends InputDictionaryItemDto {
    public SaveDictionaryItemDto(InputDictionaryItemDto dto,
                                 UUID dictionaryId) {
        super(dictionaryId, dto.getFieldValues());
    }
}