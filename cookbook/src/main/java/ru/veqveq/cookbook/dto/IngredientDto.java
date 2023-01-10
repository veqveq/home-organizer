package ru.veqveq.cookbook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class IngredientDto {
    @Schema(hidden = true)
    private UUID id;

    @Schema(description = "Название ингредиента")
    private String name;

    @Schema(description = "Количество")
    private Double count;

    @Schema(description = "Единица измерения")
    private String unit;
}
