package ru.veqveq.tables.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.veqveq.tables.dto.dictionary.DictionaryDto;
import ru.veqveq.tables.model.entity.Dictionary;

import java.util.UUID;

public interface DictionaryService {
    UUID saveDictionary(DictionaryDto dto);

    Dictionary getById(UUID uuid);

    Page<DictionaryDto> getRegistry(Pageable pageable);

    DictionaryDto update(UUID uuid, DictionaryDto dto);

    void delete(UUID uuid);
}
