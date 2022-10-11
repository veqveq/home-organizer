package ru.veqveq.backend.dto.item.input.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.veqveq.backend.dto.item.input.InputDictionaryItemDto;
import ru.veqveq.backend.validation.item.DictionaryItemValid;

import javax.validation.constraints.NotNull;
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
