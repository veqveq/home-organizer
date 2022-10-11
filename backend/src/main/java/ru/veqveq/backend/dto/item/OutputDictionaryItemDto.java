package ru.veqveq.backend.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(name = "Запись словаря")
public class OutputDictionaryItemDto {
    @Schema(description = "Идентификатор записи")
    private UUID id;

    @Schema(description = "Значения полей")
    private Map<String, Object> fieldValues;
}
