package ru.veqveq.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.cookbook.dto.TypeDto;
import ru.veqveq.cookbook.model.filter.TypeFilter;
import ru.veqveq.cookbook.service.impl.TypeService;

@Tag(name = "Контроллер типов блюд")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/types")
public class TypeController {
    private final TypeService service;

    @GetMapping("/all")
    @PageableAsQueryParam
    @Operation(summary = "Получение типов блюд")
    public Page<TypeDto> getRegister(@Parameter(hidden = true) Pageable pageable) {
        return service.getAll(pageable);
    }

    @PostMapping("/filter")
    @PageableAsQueryParam
    @Operation(summary = "Фильтр типов блюд")
    public Page<TypeDto> filter(
            @RequestBody TypeFilter filter,
            @Parameter(hidden = true) Pageable pageable) {
        return service.filter(filter,pageable);
    }
}
