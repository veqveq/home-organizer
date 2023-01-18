package ru.veqveq.cookbook.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Единица измерения ингредиента
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ingredient_unit")
public class IngredientUnit {
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Название
     */
    @Column(name = "name")
    private String name;

    /**
     * Список ингредиентов
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unit", orphanRemoval = true)
    private List<Ingredient> ingredients;

    public IngredientUnit(String name) {
        this.name = name;
    }
}
