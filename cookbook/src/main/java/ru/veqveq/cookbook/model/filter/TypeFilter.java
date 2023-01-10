package ru.veqveq.cookbook.model.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TypeFilter {
    @Schema(description = "Название")
    private String name;
    @Schema(description = "Исключения")
    private List<UUID> excludes;
}
