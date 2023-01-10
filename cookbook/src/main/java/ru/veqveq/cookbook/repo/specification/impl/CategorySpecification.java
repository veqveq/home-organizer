package ru.veqveq.cookbook.repo.specification.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.veqveq.cookbook.model.entity.Category;
import ru.veqveq.cookbook.model.filter.CategoryFilter;
import ru.veqveq.cookbook.repo.specification.AbstractSpecification;
import ru.veqveq.cookbook.util.SpecificationUtils;

@Component
public class CategorySpecification extends AbstractSpecification<Category, CategoryFilter> {
    @Override
    protected Specification<Category> addFilters(Specification<Category> specification, CategoryFilter filter) {
        return specification
                .and(SpecificationUtils.searchLike(Category.Fields.name,filter.getName()))
                .and(SpecificationUtils.searchNotIn(Category.Fields.id,filter.getExcludes()));
    }
}
