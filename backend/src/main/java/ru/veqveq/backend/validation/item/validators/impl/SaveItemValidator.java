package ru.veqveq.backend.validation.item.validators.impl;

import org.springframework.stereotype.Component;
import ru.veqveq.backend.dto.item.input.impl.SaveDictionaryItemDto;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.validation.item.validators.AbstractItemValidator;

@Component
public class SaveItemValidator extends AbstractItemValidator<SaveDictionaryItemDto> {
    @Override
    protected boolean isUniqueField(SaveDictionaryItemDto item, Dictionary dictionary, String fieldName, Object fieldValue) {
        return esService.isUniqueFieldValue(dictionary.getEsIndexName(), fieldName, fieldValue);
    }
}
