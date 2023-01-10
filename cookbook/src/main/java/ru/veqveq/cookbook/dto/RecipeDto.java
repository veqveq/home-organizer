package ru.veqveq.cookbook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RecipeDto {
    @Schema(hidden = true)
    private UUID id;

    @Schema(description = "Название")
    private String title;

    @Schema(description = "Калорийность")
    private Integer kcal;

    @Schema(description = "Белки")
    private Integer proteins;

    @Schema(description = "Жиры")
    private Integer fats;

    @Schema(description = "Углеводы")
    private Integer carbons;

    @Schema(description = "Количество порций")
    private Integer portions;

    @Schema(description = "Время готовки, минут")
    private Integer cookTime;

    @Schema(description = "Описание")
    private String description;

    @Schema(description = "Кол-во положительных отзывов")
    private Integer likes;

    @Schema(description = "Кол-во отрицательных отзывов")
    private Integer dislikes;

    @Schema(description = "Рейтинг")
    private Integer rating;

    @Schema(description = "Ссылка на картинку")
    private String imageUrl;

    @Schema(description = "Ссылка на страницу рецепта")
    private String url;

    @Schema(description = "Тип блюда")
    private TypeDto type;

    @Schema(description = "Категория блюда")
    private CategoryDto category;

    @Schema(description = "Кухня")
    private KitchenDto kitchen;

    @Schema(description = "Список ингредиентов")
    private List<IngredientDto> ingredient;
}
