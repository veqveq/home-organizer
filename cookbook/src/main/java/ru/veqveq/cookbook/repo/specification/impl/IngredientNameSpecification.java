package ru.veqveq.cookbook.repo.specification.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.veqveq.cookbook.model.entity.IngredientName;
import ru.veqveq.cookbook.model.filter.IngredientFilter;
import ru.veqveq.cookbook.repo.specification.AbstractSpecification;
import ru.veqveq.cookbook.util.SpecificationUtils;

@Component
public class IngredientNameSpecification extends AbstractSpecification<IngredientName, IngredientFilter> {
    @Override
    protected Specification<IngredientName> addFilters(Specification<IngredientName> specification, IngredientFilter filter) {
        return specification.and(SpecificationUtils.searchLike(IngredientName.Fields.name,filter.getName()));
    }
}
