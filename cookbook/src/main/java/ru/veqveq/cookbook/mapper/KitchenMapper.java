package ru.veqveq.cookbook.mapper;

import org.mapstruct.Mapper;
import ru.veqveq.cookbook.dto.KitchenDto;
import ru.veqveq.cookbook.model.entity.Kitchen;

@Mapper
public interface KitchenMapper {
    KitchenDto toDto(Kitchen kitchen);
}
