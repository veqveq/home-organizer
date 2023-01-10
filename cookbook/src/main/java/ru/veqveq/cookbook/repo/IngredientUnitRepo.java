package ru.veqveq.cookbook.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veqveq.cookbook.model.entity.IngredientUnit;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngredientUnitRepo extends JpaRepository<IngredientUnit, UUID> {
    Optional<IngredientUnit> findByName(String name);
}
