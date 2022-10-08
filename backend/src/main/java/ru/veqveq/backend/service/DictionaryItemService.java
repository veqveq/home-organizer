package ru.veqveq.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.veqveq.backend.dto.DictionaryItemDto;
import ru.veqveq.backend.model.DictionaryItemFilter;

import java.util.UUID;

public interface DictionaryItemService {
    UUID saveItem(UUID dictId, DictionaryItemDto dto);

    Page<DictionaryItemDto> findAll(UUID dictId, Pageable pageable);

    Page<DictionaryItemDto> filter(UUID dictId, DictionaryItemFilter filter, Pageable pageable);

    DictionaryItemDto update(UUID dictId, UUID uuid, DictionaryItemDto dto);

    void delete(UUID dictId,UUID uuid);
}
