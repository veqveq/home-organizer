package ru.veqveq.cookbook.model.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.veqveq.cookbook.model.ValueInterval;

import java.util.List;
import java.util.UUID;

@Data
public class RecipeFilter {
    @Schema(description = "Название")
    private String title;

    @Schema(description = "Калорийность")
    private ValueInterval<Integer> kcal;

    @Schema(description = "Белки")
    private ValueInterval<Integer> proteins;

    @Schema(description = "Жиры")
    private ValueInterval<Integer> fats;

    @Schema(description = "Углеводы")
    private ValueInterval<Integer> carbons;

    @Schema(description = "Количество порций")
    private ValueInterval<Integer> portions;

    @Schema(description = "Время готовки, минут")
    private ValueInterval<Integer> cookTime;

    @Schema(description = "Рейтинг")
    private ValueInterval<Integer> rating;

    @Schema(description = "Типы блюда")
    private List<UUID> typeIds;

    @Schema(description = "Категории блюда")
    private List<UUID> categoryIds;

    @Schema(description = "Кухни")
    private List<UUID> kitchenIds;

    @Schema(description = "Названия ингредиентов")
    private List<UUID> ingredientNameIds;

    @Schema(description = "Названия обобщенных ингредиентов")
    private List<UUID> groupIngredientNameIds;
}
