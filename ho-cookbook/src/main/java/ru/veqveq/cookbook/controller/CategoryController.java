package ru.veqveq.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.cookbook.dto.CategoryDto;
import ru.veqveq.cookbook.model.filter.CategoryFilter;
import ru.veqveq.cookbook.service.impl.CategoryService;

@Tag(name = "Контроллер категорий блюд")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService service;

    @GetMapping("/all")
    @PageableAsQueryParam
    @Operation(summary = "Получение реестра категорий блюд")
    public Page<CategoryDto> getRegister(@Parameter(hidden = true) Pageable pageable) {
        return service.getAll(pageable);
    }

    @PostMapping("/filter")
    @PageableAsQueryParam
    @Operation(summary = "Фильтр категорий блюд")
    public Page<CategoryDto> filter(
            @RequestBody CategoryFilter filter,
            @Parameter(hidden = true) Pageable pageable) {
        return service.filter(filter,pageable);
    }
}
