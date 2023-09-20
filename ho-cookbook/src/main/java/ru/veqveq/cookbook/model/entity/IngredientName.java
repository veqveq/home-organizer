package ru.veqveq.cookbook.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.*;
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

    /**
     * Список ингредиентов
     */
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "name", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    /**
     * Обобщенное название ингредиента
     */
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_ingredient_name_id")
    private IngredientName groupIngredientName;

    /**
     * Список название ингредиентов, которые обобщены данным
     */
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "groupIngredientName", fetch = FetchType.LAZY)
    private List<IngredientName> childIngredientNames;

    public IngredientName(String name) {
        this.name = name;
    }
}
