package ru.veqveq.cookbook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.veqveq.cookbook.dto.KitchenDto;
import ru.veqveq.cookbook.mapper.KitchenMapper;
import ru.veqveq.cookbook.model.filter.KitchenFilter;
import ru.veqveq.cookbook.repo.KitchenRepo;
import ru.veqveq.cookbook.repo.specification.impl.KitchenSpecification;
import ru.veqveq.cookbook.service.RecipeComponentService;

@Service
@RequiredArgsConstructor
public class KitchenService implements RecipeComponentService<KitchenDto,KitchenFilter> {
    private final KitchenRepo repo;
    private final KitchenMapper mapper;

    private final KitchenSpecification specification;

    @Override
    public Page<KitchenDto> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<KitchenDto> filter(KitchenFilter filter, Pageable pageable) {
        return repo.findAll(
                specification.byFilter(filter),
                pageable
        ).map(mapper::toDto);
    }
}
