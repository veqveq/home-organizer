package ru.veqveq.cookbook.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.cookbook.dto.CategoryDto;
import ru.veqveq.cookbook.model.entity.Category;

@Mapper
public interface CategoryMapper {
    CategoryDto toDto(Category category);
}
