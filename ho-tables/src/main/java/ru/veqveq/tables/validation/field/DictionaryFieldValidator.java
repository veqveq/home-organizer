package ru.veqveq.tables.validation.field;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.veqveq.tables.dto.dictionary.DictionaryFieldDto;
import ru.veqveq.tables.model.enumerated.DictionaryFieldType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryFieldValidator implements ConstraintValidator<DictionaryFieldValid, DictionaryFieldDto> {

    @Override
    public boolean isValid(DictionaryFieldDto value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean isValid = true;
        if (value.getType() == DictionaryFieldType.Boolean && BooleanUtils.isTrue(value.getUnique())) {
            addConstraintViolation(context, String.format("Поле: %s[%s]. Признак уникальности для логического значения запрещен", value.getName(), value.getType().getName()));
            isValid = false;
        }
        if (StringUtils.isNotBlank(value.getDefaultValue()) && !checkDefaultFieldType(value)) {
            addConstraintViolation(context, String.format("Поле: %s. Значение по умолчанию [%s] не соответствует типу поля [%s]",
                    value.getName(), value.getDefaultValue(), value.getType().getName()));
            isValid = false;
        }
        return isValid;
    }

    private boolean checkDefaultFieldType(DictionaryFieldDto dto) {
        try {
            dto.getType().getMappingFunction().apply(dto.getDefaultValue());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
