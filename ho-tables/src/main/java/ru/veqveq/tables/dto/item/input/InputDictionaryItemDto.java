package ru.veqveq.tables.dto.item.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputDictionaryItemDto {
    @NotNull(message = "Не указан идентификатор справочника")
    @Schema(description = "Идентификатор справочника", hidden = true)
    private UUID dictionaryId;

    @Schema(description = "Значения полей")
    private Map<String, Object> fieldValues;
}
