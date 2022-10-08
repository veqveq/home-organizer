package ru.veqveq.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(name = "Запись словаря")
public class DictionaryItemDto {
    @Schema(description = "Идентификатор", hidden = true)
    private String id;

    @Schema(description = "Значения полей")
    private Map<String,Object> fieldValues;
}
