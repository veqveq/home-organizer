package ru.veqveq.cookbook.model.entity;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.UUID;

/**
 * Ингредиент блюда
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table(name = "ingredient")
public class Ingredient {
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Рецепт
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    /**
     * Название ингредиента
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_name_id")
    private IngredientName name;

    /**
     * Количество
     */
    @Column(name = "count")
    private Double count;

    /**
     * Единица измерения
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private IngredientUnit unit;
}
