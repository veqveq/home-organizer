package ru.veqveq.cookbook.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    public IngredientUnit(String name) {
        this.name = name;
    }
}
