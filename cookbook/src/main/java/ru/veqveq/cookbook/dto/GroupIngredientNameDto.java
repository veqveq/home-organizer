package ru.veqveq.cookbook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GroupIngredientNameDto {
    @Schema(hidden = true)
    private UUID id;

    @Schema(description = "Наименование")
    private String name;

    @Schema(description = "Список обобщенных названий ингредиентов")
    private List<IngredientNameDto> childIngredientNames;
}
