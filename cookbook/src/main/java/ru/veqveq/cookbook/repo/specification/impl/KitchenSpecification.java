package ru.veqveq.cookbook.repo.specification.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.veqveq.cookbook.model.entity.Kitchen;
import ru.veqveq.cookbook.model.filter.KitchenFilter;
import ru.veqveq.cookbook.repo.specification.AbstractSpecification;
import ru.veqveq.cookbook.util.SpecificationUtils;

@Component
public class KitchenSpecification extends AbstractSpecification<Kitchen, KitchenFilter> {
    @Override
    protected Specification<Kitchen> addFilters(Specification<Kitchen> specification, KitchenFilter filter) {
        return specification
                .and(SpecificationUtils.searchLike(Kitchen.Fields.name,filter.getName()))
                .and(SpecificationUtils.searchNotIn(Kitchen.Fields.id,filter.getExcludes()));
    }
}
