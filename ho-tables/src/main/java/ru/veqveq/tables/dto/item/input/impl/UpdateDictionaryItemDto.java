package ru.veqveq.tables.dto.item.input.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.veqveq.tables.dto.item.input.InputDictionaryItemDto;
import ru.veqveq.tables.validation.item.DictionaryItemValid;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "Обновление записи справочника")
@DictionaryItemValid
public class UpdateDictionaryItemDto extends InputDictionaryItemDto {
    @NotNull(message = "Не указан идентификатор записи")
    @Schema(description = "Идентификатор записи")
    private UUID id;

    public UpdateDictionaryItemDto(InputDictionaryItemDto dto,
                                   UUID id,
                                   UUID dictionaryId) {
        super(dictionaryId, dto.getFieldValues());
        this.id = id;
    }
}
