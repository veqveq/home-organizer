package ru.veqveq.backend.dto.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Schema(description = "Поле справочника")
public class DictionaryFieldDto {
    @Schema(hidden = true)
    private UUID id;

    @NotNull(message = "У элемента не указана позиция")
    @Min(value = 0, message = "Некорректная позиция поля в таблице")
    @Schema(description = "Позиция поля в таблице")
    private Integer position;

    @NotBlank(message = "Не указано название поля")
    @Schema(description = "Имя поля")
    private String name;

    @NotNull(message = "Не указан тип поля")
    @Schema(description = "Тип поля")
    @Enumerated(EnumType.STRING)
    private DictionaryFieldType type;

    @Schema(description = "Признак уникальности", defaultValue = "false")
    private Boolean unique;

    @Schema(description = "Признак обязательности", defaultValue = "false")
    private Boolean required;

    @Schema(description = "Значение по умолчанию")
    private String defaultValue;
}
