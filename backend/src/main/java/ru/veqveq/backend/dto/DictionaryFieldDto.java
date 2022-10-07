package ru.veqveq.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.UUID;

@Schema(description = "Поле справочника")
@Data
public class DictionaryFieldDto {
    @Schema(hidden = true)
    private UUID id;

    @Schema(description = "Имя поля")
    private String name;

    @Schema(description = "Тип поля")
    @Enumerated(EnumType.STRING)
    private DictionaryFieldType type;

    @Schema(description = "Значение по умолчанию")
    private String defaultValue;
}
