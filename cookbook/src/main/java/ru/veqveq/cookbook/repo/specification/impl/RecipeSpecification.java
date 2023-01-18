package ru.veqveq.cookbook.repo.specification.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.veqveq.cookbook.model.entity.*;
import ru.veqveq.cookbook.model.filter.RecipeFilter;
import ru.veqveq.cookbook.repo.specification.AbstractSpecification;
import ru.veqveq.cookbook.util.SpecificationUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.CollectionUtils.isEmpty;

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
                .and(searchByIngredient(filter.getIngredientIds()));
    }

    private Specification<Recipe> searchByIngredient(
            List<UUID> ingredientIds
    ) {
        return isEmpty(ingredientIds) ? null : (root, query, cb) -> {
            Join<Object, Object> joinedEntry = root.join(Recipe.Fields.ingredient, JoinType.INNER);
            Join<Object, Object> joinedEntry2 = joinedEntry.join(Ingredient.Fields.name, JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate.getExpressions().add(cb.or(
                    joinedEntry2.get(IngredientName.Fields.id).in(ingredientIds),
                    joinedEntry2.get(IngredientName.Fields.genericNameId).in(ingredientIds))
            );
            query.groupBy(root.get("id"));
            query.having(cb.equal(cb.count(joinedEntry2.get(IngredientName.Fields.id)), ingredientIds.size()));
            return predicate;
        };
    }
}
