package ru.veqveq.tables.dto.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.veqveq.tables.model.enumerated.DictionaryFieldType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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