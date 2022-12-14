package ru.veqveq.cookbook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.veqveq.cookbook.dto.IngredientNameDto;
import ru.veqveq.cookbook.mapper.IngredientMapper;
import ru.veqveq.cookbook.model.filter.IngredientFilter;
import ru.veqveq.cookbook.repo.IngredientNameRepo;
import ru.veqveq.cookbook.repo.specification.impl.IngredientNameSpecification;
import ru.veqveq.cookbook.service.RecipeComponentService;

@Service
@RequiredArgsConstructor
public class IngredientService implements RecipeComponentService<IngredientNameDto, IngredientFilter> {
    private final IngredientNameRepo ingredientNameRepo;
    private final IngredientMapper mapper;

    private final IngredientNameSpecification ingredientNameSpecification;

    @Override
    public Page<IngredientNameDto> getAll(Pageable pageable) {
        return ingredientNameRepo.findAll(pageable).map(mapper::toNameDto);
    }

    @Override
    public Page<IngredientNameDto> filter(IngredientFilter filter, Pageable pageable) {
        return ingredientNameRepo.findAll(
                ingredientNameSpecification.byFilter(filter),
                pageable
        ).map(mapper::toNameDto);
    }
}
