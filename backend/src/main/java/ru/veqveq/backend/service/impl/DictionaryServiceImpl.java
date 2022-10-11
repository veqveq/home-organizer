package ru.veqveq.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veqveq.backend.dto.dictionary.DictionaryDto;
import ru.veqveq.backend.dto.dictionary.DictionaryFieldDto;
import ru.veqveq.backend.dto.dictionary.DictionaryMainPageDto;
import ru.veqveq.backend.exception.HOException;
import ru.veqveq.backend.mapper.DictionaryFieldMapper;
import ru.veqveq.backend.mapper.DictionaryMapper;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.repo.DictionaryRepo;
import ru.veqveq.backend.service.DictionaryService;
import ru.veqveq.backend.service.ElasticsearchService;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DictionaryServiceImpl implements DictionaryService {
    private final DictionaryRepo dictionaryRepo;
    private final DictionaryMapper dictionaryMapper;
    private final DictionaryFieldMapper fieldMapper;
    private final ElasticsearchService esService;

    @Override
    public UUID saveDictionary(DictionaryDto dto) {
        if (dictionaryRepo.existsByName(dto.getName())) {
            throw new HOException("Справочник с именем " + dto.getName() + " существует");
        }
        UUID esIndexRequest = UUID.randomUUID();
        Dictionary dictionary = dictionaryRepo.save(dictionaryMapper.toEntity(dto, esIndexRequest));
        esService.initIndex(dictionary);
        return dictionary.getId();
    }

    @Override
    public Set<DictionaryFieldDto> getFields(UUID uuid) {
        Dictionary dictionary = dictionaryRepo.findById(uuid)
                .orElseThrow(() -> new HOException(String.format("Справочник с id %s не найден", uuid)));
        return dictionary.getFields()
                .stream()
                .map(fieldMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Dictionary getById(UUID uuid) {
        return dictionaryRepo.findById(uuid)
                .orElseThrow(() -> new HOException(String.format("Справочник с id %s не найден", uuid)));
    }

    @Override
    public Page<DictionaryMainPageDto> getRegistry(Pageable pageable) {
        return dictionaryRepo.findAll(pageable)
                .map(dictionaryMapper::toMainPageDto);
    }

    @Override
    public DictionaryDto update(UUID uuid, DictionaryDto dto) {
        if (Objects.isNull(uuid)) {
            throw new HOException("Не получен идентификатор справочника");
        }
        dictionaryRepo.findByName(dto.getName()).ifPresent(exist -> {
            if (!exist.getId().equals(uuid)) {
                throw new HOException("Справочник с названием '" + dto.getName() + "' уже существует. Переименование невозможно");
            }
        });

        Dictionary dictionary = dictionaryRepo.findById(uuid)
                .orElseThrow(() -> new HOException(String.format("Справочник с id %s не найден", uuid)));

        dictionaryMapper.merge(dictionary, dto);
        UUID newIndexName = UUID.randomUUID();
        dictionary = dictionaryRepo.save(dictionary);
        esService.initIndex(dictionary, newIndexName);
        esService.migrateIndex(dictionary.getEsIndexName(), newIndexName.toString());
        esService.deleteIndex(dictionary);
        dictionary.setEsIndexName(newIndexName);
        return dictionaryMapper.toDto(dictionaryRepo.save(dictionary));
    }

    @Override
    public void delete(UUID uuid) {
        if (Objects.isNull(uuid)) {
            throw new HOException("Не получен идентификатор справочника");
        }
        Dictionary dictionary = dictionaryRepo.findById(uuid).orElseThrow(() ->
                new HOException(String.format("Справочник с идентификатором %s не найден", uuid)));
        dictionaryRepo.deleteById(dictionary.getId());
        esService.deleteIndex(dictionary);
    }
}
