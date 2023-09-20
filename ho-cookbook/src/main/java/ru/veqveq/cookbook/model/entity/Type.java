package ru.veqveq.cookbook.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Тип блюда (первое/второе/десерт/...)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@Table(name = "type")
public class Type {
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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "type",orphanRemoval = true)
    private List<Recipe> recipes;

    public Type(@NonNull String name) {
        this.name = name;
    }
}
