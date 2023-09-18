package ru.veqveq.tables.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.veqveq.tables.dto.item.OutputDictionaryItemDto;
import ru.veqveq.tables.dto.item.input.InputDictionaryItemDto;
import ru.veqveq.tables.dto.item.input.impl.SaveDictionaryItemDto;
import ru.veqveq.tables.dto.item.input.impl.UpdateDictionaryItemDto;
import ru.veqveq.tables.model.DictionaryItemFilter;
import ru.veqveq.tables.service.DictionaryItemService;

import java.util.UUID;

@Tag(name = "Контроллер значений справочников")
@RestController
@RequestMapping("/ho/api/v1/items/{dictId}")
@RequiredArgsConstructor
public class DictionaryItemController {
    private final DictionaryItemService service;

    @GetMapping("/is-unique")
    @Operation(summary = "Проверка уникальности значения")
    public Boolean checkUnique(@Parameter(description = "Идентификатор справочника")
                               @PathVariable(name = "dictId") UUID dictId,
                               @Parameter(description = "Идентификатор записи")
                               @RequestParam(name = "itemId", required = false) UUID itemId,
                               @Parameter(description = "Идентификатор поля")
                               @RequestParam(name = "fieldId") UUID fieldId,
                               @Parameter(description = "Значение поля")
                               @RequestParam(name = "fieldValue") Object fieldValue) {
        return service.checkUnique(dictId, itemId, fieldId, fieldValue);
    }

    @PostMapping
    @Operation(summary = "Сохранить запись")
    public UUID save(@Parameter(description = "Идентификатор справочника")
                     @PathVariable(name = "dictId") UUID dictId,
                     @Parameter(description = "Запись справочника")
                     @RequestBody InputDictionaryItemDto commons) {
        SaveDictionaryItemDto dto = new SaveDictionaryItemDto(commons, dictId);
        return service.saveItem(dto);
    }

    @PostMapping("/filter")
    @Operation(summary = "Фильтр")
    @PageableAsQueryParam
    public Page<OutputDictionaryItemDto> filter(@Parameter(description = "Идентификатор справочника")
                                                @PathVariable(name = "dictId") UUID dictId,
                                                @Parameter(description = "Настройки фильтра")
                                                @RequestBody DictionaryItemFilter filter,
                                                @Parameter(hidden = true)
                                                @PageableDefault Pageable pageable) {
        return service.filter(dictId, filter, pageable);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Редактирование записи")
    public OutputDictionaryItemDto update(@Parameter(description = "Идентификатор справочника")
                                          @PathVariable(name = "dictId") UUID dictId,
                                          @Parameter(description = "Идентификатор записи")
                                          @PathVariable UUID uuid,
                                          @Parameter(description = "Запись справочника")
                                          @RequestBody InputDictionaryItemDto commons) {
        UpdateDictionaryItemDto dto = new UpdateDictionaryItemDto(commons, uuid, dictId);
        return service.update(dto);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Удалить запись")
    public void delete(@Parameter(description = "Идентификатор справочника")
                       @PathVariable(name = "dictId") UUID dictId,
                       @Parameter(description = "Идентификатор записи")
                       @PathVariable UUID uuid) {
        service.delete(dictId, uuid);
    }
}
