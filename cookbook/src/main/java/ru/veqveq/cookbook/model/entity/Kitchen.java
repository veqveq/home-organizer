package ru.veqveq.cookbook.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.UUID;

/**
 * Название кухни
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@Table(name = "kitchen")
public class Kitchen {
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Название
     */
    @Column(name = "name")
    private String name;

    public Kitchen(String name) {
        this.name = name;
    }
}
