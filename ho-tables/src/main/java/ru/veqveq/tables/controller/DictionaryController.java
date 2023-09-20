package ru.veqveq.tables.controller;

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
import ru.veqveq.tables.dto.dictionary.DictionaryDto;
import ru.veqveq.tables.mapper.DictionaryMapper;
import ru.veqveq.tables.model.AuditorAwareEntity;
import ru.veqveq.tables.service.DictionaryService;

import jakarta.validation.Valid;
import java.util.UUID;

@Tag(name = "Контроллер справочников")
@RestController
@RequestMapping("/ho/api/v1/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;
    private final DictionaryMapper mapper;

    @PostMapping
    @Operation(summary = "Сохранить справочник")
    public UUID save(
            @Parameter(description = "Запись справочник")
            @Valid @RequestBody DictionaryDto dto) {
        return service.saveDictionary(dto);
    }

    @GetMapping
    @Operation(summary = "Получить реестр справочников")
    @PageableAsQueryParam
    public Page<DictionaryDto> findAll(
            @Parameter(hidden = true)
            @PageableDefault(sort = {AuditorAwareEntity.Fields.createStamp}, direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getRegistry(pageable);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Получить справочник по идентификатору")
    public DictionaryDto getById(
            @Parameter(description = "Идентификатор справочника")
            @PathVariable(name = "uuid") UUID uuid) {
        return mapper.toDto(service.getById(uuid));
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Редактирование справочника")
    public DictionaryDto update(@Parameter(description = "Идентификатор справочника")
                                @PathVariable UUID uuid,
                                @Parameter(description = "Справочник")
                                @Valid @RequestBody DictionaryDto dto) {
        return service.update(uuid, dto);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Удалить справочник")
    public void delete(@Parameter(description = "Идентификатор справочника")
                       @PathVariable UUID uuid) {
        service.delete(uuid);
    }
}
