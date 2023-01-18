package ru.veqveq.cookbook.repo.specification.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.veqveq.cookbook.dto.IngredientNameDto;
import ru.veqveq.cookbook.model.entity.*;
import ru.veqveq.cookbook.model.filter.RecipeFilter;
import ru.veqveq.cookbook.repo.specification.AbstractSpecification;
import ru.veqveq.cookbook.util.SpecificationUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
                        List.of(Recipe.Fields.type),
                        Type.Fields.id,
                        filter.getTypeIds()))
                .and(SpecificationUtils.searchInJoinedObjectIn(
                        List.of(Recipe.Fields.category),
                        Category.Fields.id,
                        filter.getCategoryIds()))
                .and(SpecificationUtils.searchInJoinedObjectIn(
                        List.of(Recipe.Fields.kitchen),
                        Kitchen.Fields.id,
                        filter.getKitchenIds()))
                .and(addIngredientFilter(filter.getIngredients()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Specification<Recipe> addIngredientFilter(Map<UUID, List<IngredientNameDto>> ingredientsMap){
        return ingredientsMap.values().stream().map(name->(Specification)SpecificationUtils.searchInJoinedObjectIn(
                List.of(Recipe.Fields.ingredient, Ingredient.Fields.name),
                IngredientName.Fields.id,
                name.stream().map(IngredientNameDto::getId).collect(Collectors.toList())
        )).reduce((s1,s2)->s1.and(s2)).orElse(null);
    }
}
