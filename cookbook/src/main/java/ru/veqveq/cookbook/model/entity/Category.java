package ru.veqveq.cookbook.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Категория блюда (суп/паста/пицца/...)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Название
     */
    @Column(name = "name")
    private String name;

    /**
     * Список рецептов
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", orphanRemoval = true)
    private List<Recipe> recipes;

    public Category(String name) {
        this.name = name;
    }
}
