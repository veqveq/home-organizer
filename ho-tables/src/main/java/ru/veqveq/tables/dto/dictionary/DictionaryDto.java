package ru.veqveq.tables.dto.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;
import ru.veqveq.tables.validation.field.DictionaryFieldValid;

import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "Справочник")
public class DictionaryDto {
    @Schema(hidden = true)
    private UUID id;

    @NotBlank(message = "Название справочника не должно быть пустым")
    @Schema(description = "Название справочника")
    private String name;

    @Schema(description = "Описание справочника")
    private String description;

    @NotEmpty(message = "Список полей справочника не должен быть пустым")
    @ToString.Exclude
    @Schema(description = "Список полей справочника")
    private List<@DictionaryFieldValid @Valid DictionaryFieldDto> fields;
}
