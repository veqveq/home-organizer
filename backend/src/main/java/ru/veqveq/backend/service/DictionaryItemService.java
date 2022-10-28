package ru.veqveq.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.veqveq.backend.dto.item.OutputDictionaryItemDto;
import ru.veqveq.backend.dto.item.input.impl.SaveDictionaryItemDto;
import ru.veqveq.backend.dto.item.input.impl.UpdateDictionaryItemDto;
import ru.veqveq.backend.model.DictionaryItemFilter;

import javax.validation.Valid;
import java.util.UUID;

public interface DictionaryItemService {
    UUID saveItem(@Valid SaveDictionaryItemDto dto);

    Page<OutputDictionaryItemDto> filter(UUID dictId, DictionaryItemFilter filter, Pageable pageable);

    OutputDictionaryItemDto update(@Valid UpdateDictionaryItemDto dto);

    void delete(UUID dictId,UUID uuid);

    boolean checkUnique(UUID dictionaryId, UUID itemId, UUID fieldId, Object fieldValue);
}
