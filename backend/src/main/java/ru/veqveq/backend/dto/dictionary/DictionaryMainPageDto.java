package ru.veqveq.backend.dto.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(name = "Представление справочника для реестра")
public class DictionaryMainPageDto {
    @Schema(description = "Идентификатор справочника")
    private UUID id;

    @Schema(description = "Название справочника")
    private String name;
}
