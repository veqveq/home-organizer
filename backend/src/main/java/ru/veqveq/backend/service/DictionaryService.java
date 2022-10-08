package ru.veqveq.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.veqveq.backend.dto.DictionaryDto;
import ru.veqveq.backend.dto.DictionaryFieldDto;
import ru.veqveq.backend.dto.DictionaryMainPageDto;
import ru.veqveq.backend.model.entity.Dictionary;

import java.util.Set;
import java.util.UUID;

public interface DictionaryService {
    UUID saveDictionary(DictionaryDto dto);

    Dictionary getById(UUID uuid);

    Set<DictionaryFieldDto> getFields(UUID uuid);

    Page<DictionaryMainPageDto> getRegistry(Pageable pageable);

    DictionaryDto update(UUID uuid, DictionaryDto dto);

    void delete(UUID uuid);
}
