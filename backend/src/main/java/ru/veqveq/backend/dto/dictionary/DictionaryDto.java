package ru.veqveq.backend.dto.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.veqveq.backend.validation.field.DictionaryFieldValid;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Список полей справочника не должен быть пустым")
    @Schema(description = "Список полей справочника")
    private List<@DictionaryFieldValid @Valid DictionaryFieldDto> fields;
}
