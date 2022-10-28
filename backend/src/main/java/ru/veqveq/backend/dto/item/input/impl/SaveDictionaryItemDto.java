package ru.veqveq.backend.dto.item.input.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.veqveq.backend.dto.item.input.InputDictionaryItemDto;
import ru.veqveq.backend.validation.item.DictionaryItemValid;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "Сохранение новой записи")
@DictionaryItemValid
public class SaveDictionaryItemDto extends InputDictionaryItemDto {
    public SaveDictionaryItemDto(InputDictionaryItemDto dto,
                                 UUID dictionaryId) {
        super(dictionaryId, dto.getFieldValues());
    }
}
