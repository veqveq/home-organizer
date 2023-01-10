package ru.veqveq.cookbook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.veqveq.cookbook.dto.CategoryDto;
import ru.veqveq.cookbook.mapper.CategoryMapper;
import ru.veqveq.cookbook.model.filter.CategoryFilter;
import ru.veqveq.cookbook.repo.CategoryRepo;
import ru.veqveq.cookbook.repo.specification.impl.CategorySpecification;
import ru.veqveq.cookbook.service.RecipeComponentService;

@Service
@RequiredArgsConstructor
public class CategoryService implements RecipeComponentService<CategoryDto, CategoryFilter> {
    private final CategoryRepo repo;
    private final CategorySpecification specification;
    private final CategoryMapper mapper;

    @Override
    public Page<CategoryDto> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<CategoryDto> filter(CategoryFilter filter, Pageable pageable) {
        return repo.findAll(specification.byFilter(filter), pageable).map(mapper::toDto);
    }
}
