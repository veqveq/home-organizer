package ru.veqveq.backend.validation.item.validators.impl;

import org.springframework.stereotype.Component;
import ru.veqveq.backend.dto.item.input.impl.UpdateDictionaryItemDto;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.validation.item.validators.AbstractItemValidator;

@Component
public class UpdateItemValidator extends AbstractItemValidator<UpdateDictionaryItemDto> {
    @Override
    protected boolean isUniqueField(UpdateDictionaryItemDto item, Dictionary dictionary, String fieldName, Object fieldValue) {
        return esService.isUniqueFieldValue(dictionary.getEsIndexName(), item.getId(), fieldName, fieldValue);
    }
}
