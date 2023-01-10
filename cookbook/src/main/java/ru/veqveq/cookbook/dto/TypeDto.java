package ru.veqveq.cookbook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class TypeDto {
    @Schema(hidden = true)
    private UUID id;

    @Schema(description = "Наименование")
    private String name;
}
