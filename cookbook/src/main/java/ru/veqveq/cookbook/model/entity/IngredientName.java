package ru.veqveq.cookbook.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Название ингредиента
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode
@Table(name = "ingredient_name")
public class IngredientName {
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Название
     */
    @Column(name = "name")
    private String name;

    @Column(name = "generic_name_id")
    private UUID genericNameId;

    /**
     * Список ингредиентов
     */
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "name", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    public IngredientName(String name) {
        this.name = name;
    }
}
