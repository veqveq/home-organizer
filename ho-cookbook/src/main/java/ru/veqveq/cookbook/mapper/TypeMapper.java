package ru.veqveq.cookbook.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.cookbook.dto.TypeDto;
import ru.veqveq.cookbook.model.entity.Type;

@Mapper
public interface TypeMapper {
    TypeDto toDto(Type source);
}
