package ru.veqveq.cookbook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.veqveq.cookbook.dto.TypeDto;
import ru.veqveq.cookbook.mapper.TypeMapper;
import ru.veqveq.cookbook.model.filter.TypeFilter;
import ru.veqveq.cookbook.repo.TypeRepo;
import ru.veqveq.cookbook.repo.specification.impl.TypeSpecification;
import ru.veqveq.cookbook.service.RecipeComponentService;

@Service
@RequiredArgsConstructor
public class TypeService implements RecipeComponentService<TypeDto, TypeFilter> {
    private final TypeRepo repo;
    private final TypeMapper mapper;
    private final TypeSpecification specifiacation;

    @Override
    public Page<TypeDto> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<TypeDto> filter(TypeFilter filter, Pageable pageable) {
        return repo.findAll(specifiacation.byFilter(filter), pageable).map(mapper::toDto);
    }
}
