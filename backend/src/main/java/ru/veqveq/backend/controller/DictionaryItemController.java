package ru.veqveq.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.backend.dto.DictionaryItemDto;
import ru.veqveq.backend.model.entity.DictionaryItem;
import ru.veqveq.backend.service.DictionaryItemService;

import java.util.UUID;

@Tag(name = "Контроллер значений справочников")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class DictionaryItemController {
    private final DictionaryItemService service;

    @PostMapping
    @Operation(summary = "Сохранить запись")
    public UUID save(
            @Parameter(description = "Запись словаря")
            @RequestBody DictionaryItemDto dto) {
        return service.saveItem(dto);
    }

    @GetMapping
    @Operation(summary = "Получить все записи")
    @PageableAsQueryParam
    public Page<DictionaryItemDto> findAll(
            @Parameter(hidden = true)
            @PageableDefault(sort = {DictionaryItem.Fields.item}, direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/filter")
    @Operation(summary = "Фильтр")
    @PageableAsQueryParam
    public Page<DictionaryItemDto> filter(
            @Parameter(description = "Фраза для поиска") @RequestParam String phrase,
            @Parameter(hidden = true)
            @PageableDefault Pageable pageable) {
        return service.filter(phrase, pageable);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Редактирование записи")
    public void update(@Parameter(description = "Идентификатор записи")
                       @PathVariable UUID uuid,
                       @Parameter(description = "Запись словаря")
                       @RequestBody DictionaryItemDto dto) {
        service.update(uuid, dto);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Удалить запись")
    public void delete(@Parameter(description = "Идентификатор записи")
                       @PathVariable UUID uuid) {
        service.delete(uuid);
    }
}
