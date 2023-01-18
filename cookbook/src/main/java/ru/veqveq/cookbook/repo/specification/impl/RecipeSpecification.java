package ru.veqveq.cookbook.repo.specification.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.veqveq.cookbook.model.entity.*;
import ru.veqveq.cookbook.model.filter.RecipeFilter;
import ru.veqveq.cookbook.repo.specification.AbstractSpecification;
import ru.veqveq.cookbook.util.SpecificationUtils;

import java.util.List;

@Component
public class RecipeSpecification extends AbstractSpecification<Recipe, RecipeFilter> {
    @Override
    protected Specification<Recipe> addFilters(Specification<Recipe> specification, RecipeFilter filter) {
        return specification
                .and(SpecificationUtils.searchLike(Recipe.Fields.title, filter.getTitle()))
                .and(SpecificationUtils.searchInInterval(Recipe.Fields.kcal, filter.getKcal()))
                .and(SpecificationUtils.searchInInterval(Recipe.Fields.proteins, filter.getProteins()))
                .and(SpecificationUtils.searchInInterval(Recipe.Fields.fats, filter.getFats()))
                .and(SpecificationUtils.searchInInterval(Recipe.Fields.carbons, filter.getCarbons()))
                .and(SpecificationUtils.searchInInterval(Recipe.Fields.cookTime, filter.getCookTime()))
                .and(SpecificationUtils.searchInInterval(Recipe.Fields.rating, filter.getRating()))
                .and(SpecificationUtils.searchInInterval(Recipe.Fields.portions, filter.getPortions()))
                .and(SpecificationUtils.searchInJoinedObjectIn(
                        Recipe.Fields.type,
                        Type.Fields.id,
                        filter.getTypeIds()))
                .and(SpecificationUtils.searchInJoinedObjectIn(
                        Recipe.Fields.category,
                        Category.Fields.id,
                        filter.getCategoryIds()))
                .and(SpecificationUtils.searchInJoinedObjectIn(
                        Recipe.Fields.kitchen,
                        Kitchen.Fields.id,
                        filter.getKitchenIds()))
                .and(SpecificationUtils.searchWithJoinCollectionFullIn(
                        List.of(Recipe.Fields.ingredient, Ingredient.Fields.name),
                        IngredientName.Fields.id,
                        filter.getIngredientNameIds()))
                .and(SpecificationUtils.searchWithJoinCollectionFullIn(
                        List.of(Recipe.Fields.ingredient,
                                Ingredient.Fields.name,
                                IngredientName.Fields.groupIngredientName),
                        IngredientName.Fields.id,
                        filter.getIngredientNameIds()));
    }
}
