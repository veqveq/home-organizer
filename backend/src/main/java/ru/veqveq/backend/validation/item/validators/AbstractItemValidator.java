package ru.veqveq.backend.validation.item.validators;

import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.veqveq.backend.dto.item.input.InputDictionaryItemDto;
import ru.veqveq.backend.model.entity.Dictionary;
import ru.veqveq.backend.model.entity.DictionaryField;
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;
import ru.veqveq.backend.service.DictionaryItemService;
import ru.veqveq.backend.service.DictionaryService;
import ru.veqveq.backend.service.ElasticsearchService;
import ru.veqveq.backend.util.ElasticUtils;

import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Setter(onMethod_ = @Autowired)
public abstract class AbstractItemValidator<T extends InputDictionaryItemDto> {
    protected DictionaryService dictionaryService;
    private DictionaryItemService itemService;
    protected ElasticsearchService esService;

    protected abstract boolean isUniqueField(T item, Dictionary dictionary, String fieldName, Object fieldValue);

    public boolean check(InputDictionaryItemDto dto) {
        Type type = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return dto.getClass() == type;
    }

    public boolean validate(T item, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean valid;
        Dictionary dictionary = dictionaryService.getById(item.getDictionaryId());
        Map<String, DictionaryField> fieldsMap = dictionary.getFields()
                .stream()
                .collect(Collectors.toMap(fld -> fld.getId().toString(), Function.identity()));

        valid = checkRequiredFields(item, context, fieldsMap);

        for (Map.Entry<String, Object> field : item.getFieldValues().entrySet()) {
            if (!checkFieldIsExist(field.getKey(), fieldsMap, context)) {
                valid = false;
                continue;
            }
            DictionaryField fieldScheme = fieldsMap.get(field.getKey());
            if (!checkUniqueField(fieldScheme, dictionary, item, field.getKey(), field.getValue(), context)) {
                valid = false;
            }
            if (!checkFieldValueType(fieldScheme, field.getValue(), context)) {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Проверка заполнения обязательных полей
     * 1. Поле не заполнено и есть defaultValue -> добавляем в item.fieldValues поле defaultValue
     * 2. Поле не обязательное и нет defaultValue -> валидация валится
     */
    private boolean checkRequiredFields(T item, ConstraintValidatorContext context, Map<String, DictionaryField> fieldsMap) {
        AtomicBoolean valid = new AtomicBoolean(true);
        fieldsMap.entrySet().stream()
                .filter(fld -> fld.getValue().getRequired() && !item.getFieldValues().containsKey(fld.getKey()))
                .forEach(fld -> {
                    valid.set(false);
                    addConstraintViolation(context, String.format("Обязательное поле [%s] не заполнено", fld.getValue().getName()));
                });
        return valid.get();
    }

    /**
     * Проверка существования поля в индексе Elasticsearch
     */
    private boolean checkFieldIsExist(String fieldName, Map<String, DictionaryField> fieldsMap, ConstraintValidatorContext context) {
        if (!fieldsMap.containsKey(fieldName)) {
            addConstraintViolation(context, String.format("Неизвестное поле [%s]", fieldName));
            return false;
        }
        return true;
    }

    /**
     * Проверка уникальности поля в индексе Elasticsearch
     */
    private boolean checkUniqueField(DictionaryField fieldScheme, Dictionary dictionary, T item, String name, Object value, ConstraintValidatorContext context) {
        if (BooleanUtils.isTrue(fieldScheme.getUnique())) {
            if (fieldScheme.getType() == DictionaryFieldType.Text) {
                name = StringUtils.joinWith(".",name, ElasticUtils.RAW_INDEX_FIELD_PREFIX);
            }
            if (!isUniqueField(item, dictionary, name, value)) {
                addConstraintViolation(context, String.format("Значение [%s:%s] не уникально", fieldScheme.getName(), value));
                return false;
            }
        }
        return true;
    }

    /**
     * Проверка типа полученных данных в поле
     */
    private boolean checkFieldValueType(DictionaryField fieldScheme, Object fieldValue, ConstraintValidatorContext context) {
        try {
            fieldScheme.getType().getMappingFunction().apply(fieldValue.toString());
        } catch (Exception e) {
            addConstraintViolation(context, String.format("Значение [%s] поля [%s] имеет неправильный формат. Тип поля - %s", fieldValue, fieldScheme.getName(), fieldScheme.getType().getName()));
            return false;
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
