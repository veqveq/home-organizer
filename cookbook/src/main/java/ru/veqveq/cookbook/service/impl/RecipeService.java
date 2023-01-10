package ru.veqveq.cookbook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.veqveq.cookbook.dto.RecipeDto;
import ru.veqveq.cookbook.mapper.RecipeMapper;
import ru.veqveq.cookbook.model.filter.RecipeFilter;
import ru.veqveq.cookbook.repo.RecipeRepo;
import ru.veqveq.cookbook.repo.specification.impl.RecipeSpecification;
import ru.veqveq.cookbook.service.RecipeComponentService;

@Service
@RequiredArgsConstructor
public class RecipeService implements RecipeComponentService<RecipeDto, RecipeFilter> {
    private final RecipeRepo recipeRepo;
    private final RecipeSpecification specification;
    private final RecipeMapper mapper;

    @Override
    public Page<RecipeDto> getAll(Pageable pageable) {
        return recipeRepo.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<RecipeDto> filter(RecipeFilter filter, Pageable pageable) {
        return recipeRepo.findAll(specification.byFilter(filter), pageable).map(mapper::toDto);
    }
}
