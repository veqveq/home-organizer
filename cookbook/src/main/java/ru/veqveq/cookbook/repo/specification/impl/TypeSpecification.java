package ru.veqveq.cookbook.repo.specification.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.veqveq.cookbook.model.entity.Type;
import ru.veqveq.cookbook.model.filter.TypeFilter;
import ru.veqveq.cookbook.repo.specification.AbstractSpecification;
import ru.veqveq.cookbook.util.SpecificationUtils;

@Component
public class TypeSpecification extends AbstractSpecification<Type, TypeFilter> {
    @Override
    protected Specification<Type> addFilters(Specification<Type> specification, TypeFilter filter) {
        return specification.and(SpecificationUtils.searchLike(Type.Fields.name,filter.getName()));
    }
}
