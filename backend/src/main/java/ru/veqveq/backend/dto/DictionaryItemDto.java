package ru.veqveq.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Запись словаря")
public class DictionaryItemDto {
    @Schema(description = "Идентификатор", hidden = true)
    private String id;

    @Schema(description = "Понятие")
    private String item;

    @Schema(description = "Описание")
    private String description;
}
