package ru.veqveq.cookbook.model.entity;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Рецепт
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Название
     */
    @Column(name = "title")
    private String title;

    /**
     * Калорийность
     */
    @Column(name = "kcal")
    private Integer kcal;

    /**
     * Белки
     */
    @Column(name = "prot")
    private Integer proteins;

    /**
     * Жиры
     */
    @Column(name = "fats")
    private Integer fats;

    /**
     * Углеводы
     */
    @Column(name = "carb")
    private Integer carbons;

    /**
     * Количество порций
     */
    @Column(name = "portion")
    private Integer portions;

    /**
     * Время готовки, минут
     */
    @Column(name = "cook_time")
    private Double cookTime;

    /**
     * Описание
     */
    @Column(name = "description")
    private String description;

    /**
     * Кол-во положительных отзывов
     */
    @Column(name = "likes")
    private Integer likes;

    /**
     * Кол-во отрицательных отзывов
     */
    @Column(name = "dislikes")
    private Integer dislikes;

    /**
     * Рейтинг
     */
    @Column(name = "rating")
    private Integer rating;

    /**
     * Ссылка на картинку
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Ссылка на страницу рецепта
     */
    @Column(name = "web_url")
    private String url;

    /**
     * Тип блюда
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "type_id")
    private Type type;

    /**
     * Категория блюда
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Кухня
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "kitchen_id")
    private Kitchen kitchen;

    /**
     * Список ингредиентов
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredient;

    /**
     * Парсинг полностью завершен
     */
    @Column(name = "completed")
    private Boolean completed;
}
