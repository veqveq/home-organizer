package ru.veqveq.tables.validation.item.validators.impl;

import org.springframework.stereotype.Component;
import ru.veqveq.tables.dto.item.input.impl.SaveDictionaryItemDto;
import ru.veqveq.tables.model.entity.Dictionary;
import ru.veqveq.tables.validation.item.validators.AbstractItemValidator;

@Component
public class SaveItemValidator extends AbstractItemValidator<SaveDictionaryItemDto> {
    @Override
    protected boolean isUniqueField(SaveDictionaryItemDto item, Dictionary dictionary, String fieldName, Object fieldValue) {
        return esService.isUniqueFieldValue(dictionary.getEsIndexName(), null, fieldName, fieldValue);
    }
}
