package ru.veqveq.tables.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veqveq.tables.dto.dictionary.DictionaryDto;
import ru.veqveq.tables.exception.HoException;
import ru.veqveq.tables.exception.HoNotFoundException;
import ru.veqveq.tables.mapper.DictionaryMapper;
import ru.veqveq.tables.model.entity.Dictionary;
import ru.veqveq.tables.repo.DictionaryRepo;
import ru.veqveq.tables.service.DictionaryService;
import ru.veqveq.tables.service.ElasticsearchService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DictionaryServiceImpl implements DictionaryService {
    private final DictionaryRepo dictionaryRepo;
    private final DictionaryMapper dictionaryMapper;
    private final ElasticsearchService esService;

    @Override
    public UUID saveDictionary(DictionaryDto dto) {
        log.info("Saving dictionary {} has started", dto.toString());
        if (dictionaryRepo.existsByName(dto.getName())) {
            throw new HoException(String.format("Справочник с именем %s существует", dto.getName()));
        }
        UUID esIndexRequest = UUID.randomUUID();
        Dictionary dictionary = dictionaryRepo.save(dictionaryMapper.toEntity(dto, esIndexRequest));
        esService.initIndex(dictionary);
        log.info("Saving dictionary {} successful", dto.toString());
        return dictionary.getId();
    }

    @Override
    public Dictionary getById(UUID uuid) {
        return dictionaryRepo.findById(uuid)
                .orElseThrow(() -> new HoException(String.format("Справочник с id %s не найден", uuid)));
    }

    @Override
    public Page<DictionaryDto> getRegistry(Pageable pageable) {
        return dictionaryRepo.findAll(pageable)
                .map(dictionaryMapper::toDto);
    }

    @Override
    public DictionaryDto update(UUID uuid, DictionaryDto dto) {
        log.info("Updating dictionary {} has started", dto.toString());
        dictionaryRepo.findByName(dto.getName()).ifPresent(exist -> {
            if (!exist.getId().equals(uuid)) {
                throw new HoException(
                        String.format("Справочник с названием '%s' уже существует. Переименование невозможно", dto.getName()));
            }
        });

        Dictionary dictionary = dictionaryRepo.findById(uuid)
                .orElseThrow(() -> new HoNotFoundException(String.format("Справочник с id %s не найден", uuid)));

        dictionaryMapper.merge(dictionary, dto);
        UUID newIndexName = UUID.randomUUID();
        dictionary = dictionaryRepo.save(dictionary);
        esService.initIndex(dictionary, newIndexName);
        esService.migrateIndex(dictionary.getEsIndexName(), newIndexName.toString());
        esService.deleteIndex(dictionary);
        dictionary.setEsIndexName(newIndexName);
        log.info("Updating dictionary {} successful", dto.toString());
        return dictionaryMapper.toDto(dictionaryRepo.save(dictionary));
    }

    @Override
    public void delete(UUID uuid) {
        Dictionary dictionary = dictionaryRepo.findById(uuid).orElseThrow(() ->
                new HoNotFoundException(String.format("Справочник с идентификатором %s не найден", uuid)));
        log.info("Deleting dictionary {} has started", dictionary.getName());
        dictionaryRepo.deleteById(dictionary.getId());
        esService.deleteIndex(dictionary);
        log.info("Deleting dictionary {} successful", dictionary.getName());
    }
}
