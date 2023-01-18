package ru.veqveq.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.cookbook.dto.GroupIngredientNameDto;
import ru.veqveq.cookbook.model.filter.IngredientFilter;
import ru.veqveq.cookbook.service.impl.IngredientService;

@Tag(name = "Контроллер ингредиентов")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingredients")
public class IngredientNameController {
    private final IngredientService ingredientService;

    @GetMapping("/all")
    @PageableAsQueryParam
    @Operation(summary = "Получение реестра ингредиентов")
    public Page<GroupIngredientNameDto> getRegister(@Parameter(hidden = true) Pageable pageable) {
        return ingredientService.getAll(pageable);
    }

    @PostMapping("/filter")
    @PageableAsQueryParam
    @Operation(summary = "Фильтр ингредиентов")
    public Page<GroupIngredientNameDto> filter(
            @RequestBody IngredientFilter filter,
            @Parameter(hidden = true) Pageable pageable) {
        return ingredientService.filter(filter,pageable);
    }
}
