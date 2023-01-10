package ru.veqveq.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.cookbook.dto.KitchenDto;
import ru.veqveq.cookbook.model.filter.KitchenFilter;
import ru.veqveq.cookbook.service.impl.KitchenService;

@Tag(name = "Контроллер кухонь")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kitchens")
public class KitchenController {
    private final KitchenService service;

    @GetMapping("/all")
    @PageableAsQueryParam
    @Operation(summary = "Получение реестра кухонь")
    public Page<KitchenDto> getRegister(@Parameter(hidden = true) Pageable pageable) {
        return service.getAll(pageable);
    }

    @PostMapping("/filter")
    @PageableAsQueryParam
    @Operation(summary = "Фильтр кухонь")
    public Page<KitchenDto> filter(
            @RequestBody KitchenFilter filter,
            @Parameter(hidden = true) Pageable pageable) {
        return service.filter(filter,pageable);
    }
}
