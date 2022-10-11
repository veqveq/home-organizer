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
import ru.veqveq.backend.dto.dictionary.DictionaryDto;
import ru.veqveq.backend.dto.dictionary.DictionaryFieldDto;
import ru.veqveq.backend.dto.dictionary.DictionaryMainPageDto;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.service.DictionaryService;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Контроллер справочников")
@RestController
@RequestMapping("/ho/api/v1/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;

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
    public Page<DictionaryMainPageDto> findAll(
            @Parameter(hidden = true)
            @PageableDefault(sort = {Dictionary.Fields.name}, direction = Sort.Direction.ASC) Pageable pageable) {
        return service.getRegistry(pageable);
    }

    @GetMapping("/{uuid}/fields")
    @Operation(summary = "Получить поля справочника по идентификатору")
    public Set<DictionaryFieldDto> getFields(
            @Parameter(description = "Идентификатор справочника")
            @PathVariable(name = "uuid") UUID uuid) {
        return service.getFields(uuid);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Редактирование справочника")
    public DictionaryDto update(@Parameter(description = "Идентификатор справочника")
                                @PathVariable UUID uuid,
                                @Parameter(description = "Справочник")
                                @RequestBody DictionaryDto dto) {
        return service.update(uuid, dto);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Удалить справочник")
    public void delete(@Parameter(description = "Идентификатор справочника")
                       @PathVariable UUID uuid) {
        service.delete(uuid);
    }
}
