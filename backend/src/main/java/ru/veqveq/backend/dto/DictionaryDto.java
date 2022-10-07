package ru.veqveq.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "Справочник")
public class DictionaryDto {
    @Schema(hidden = true)
    private UUID id;

    @Schema(description = "Название справочника")
    private String name;

    @Schema(description = "Список полей справочника")
    private List<DictionaryFieldDto> fields;
}
