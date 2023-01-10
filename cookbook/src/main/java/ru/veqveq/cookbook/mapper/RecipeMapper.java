package ru.veqveq.cookbook.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.cookbook.dto.RecipeDto;
import ru.veqveq.cookbook.model.entity.Recipe;

@Mapper(uses = {
        CategoryMapper.class,
        IngredientMapper.class,
        KitchenMapper.class,
        TypeMapper.class,
        IngredientMapper.class
})
public interface RecipeMapper {
    RecipeDto toDto(Recipe recipe);
}
