package ru.veqveq.backend.validation.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veqveq.backend.dto.item.input.InputDictionaryItemDto;
import ru.veqveq.backend.dto.item.input.impl.SaveDictionaryItemDto;
import ru.veqveq.backend.dto.item.input.impl.UpdateDictionaryItemDto;
import ru.veqveq.backend.exception.HoException;
import ru.veqveq.backend.validation.item.validators.AbstractItemValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryItemValidator implements ConstraintValidator<DictionaryItemValid, InputDictionaryItemDto> {
    private final List<AbstractItemValidator> validators;

    @Override
    public boolean isValid(InputDictionaryItemDto inputDictionaryItemDto, ConstraintValidatorContext context) {
        AbstractItemValidator validator = validators.stream()
                .filter(val -> val.check(inputDictionaryItemDto))
                .findFirst()
                .orElseThrow(() -> new HoException("Не найден валидатор сущности"));

        if (inputDictionaryItemDto instanceof SaveDictionaryItemDto) {
            log.info("It is save operation");
            SaveDictionaryItemDto dto = (SaveDictionaryItemDto) inputDictionaryItemDto;
            return validator.validate(dto, context);
        } else if (inputDictionaryItemDto instanceof UpdateDictionaryItemDto) {
            log.info("It is update operation");
            UpdateDictionaryItemDto dto = (UpdateDictionaryItemDto) inputDictionaryItemDto;
            return validator.validate(dto, context);
        }
        return false;
    }


}
