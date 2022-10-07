package ru.veqveq.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.veqveq.backend.dto.DictionaryItemDto;

import java.util.UUID;

public interface DictionaryItemService {
    UUID saveItem(DictionaryItemDto dto);

    Page<DictionaryItemDto> findAll(Pageable pageable);

    Page<DictionaryItemDto> filter(String phrase, Pageable pageable);

    DictionaryItemDto update(UUID uuid, DictionaryItemDto dto);

    void delete(UUID uuid);
}
