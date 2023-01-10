package ru.veqveq.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.cookbook.dto.RecipeDto;
import ru.veqveq.cookbook.model.filter.RecipeFilter;
import ru.veqveq.cookbook.service.impl.RecipeService;

@Tag(name = "Контроллер рецептов")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    private final RecipeService service;

    @GetMapping("/all")
    @PageableAsQueryParam
    @Operation(summary = "Получение рецептов")
    public Page<RecipeDto> getRegister(@Parameter(hidden = true) Pageable pageable) {
        return service.getAll(pageable);
    }

    @PostMapping("/filter")
    @PageableAsQueryParam
    @Operation(summary = "Фильтр рецептов")
    public Page<RecipeDto> filter(
            @RequestBody RecipeFilter filter,
            @Parameter(hidden = true) Pageable pageable) {
        return service.filter(filter,pageable);
    }
}
