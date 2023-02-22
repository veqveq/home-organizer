package ru.veqveq.cookbook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class IngredientNameDto {
    @Schema(hidden = true)
    private UUID id;

    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Идентификатор группы ингрединтов")
    private IngredientNameDto group;
}
