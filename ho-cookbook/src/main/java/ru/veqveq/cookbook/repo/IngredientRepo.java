package ru.veqveq.cookbook.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.veqveq.cookbook.model.entity.Ingredient;

import java.util.UUID;

@Repository
public interface IngredientRepo extends JpaRepository<Ingredient, UUID>, JpaSpecificationExecutor<Ingredient> {
}
