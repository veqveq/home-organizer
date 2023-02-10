package ru.veqveq.cookbook.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.veqveq.cookbook.dto.GroupIngredientNameDto;
import ru.veqveq.cookbook.dto.IngredientDto;
import ru.veqveq.cookbook.dto.IngredientNameDto;
import ru.veqveq.cookbook.model.entity.Ingredient;
import ru.veqveq.cookbook.model.entity.IngredientName;

@Mapper
public interface IngredientMapper {
    @Mapping(target = "groupId", source = "groupIngredientName.id")
    IngredientNameDto toNameDto(IngredientName source);

    GroupIngredientNameDto toGroupNameDto(IngredientName source);

    @Mapping(target = "unit", source = "unit.name")
    IngredientDto toDto(Ingredient source);
}
