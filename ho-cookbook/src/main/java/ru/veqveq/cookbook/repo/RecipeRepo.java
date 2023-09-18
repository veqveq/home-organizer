package ru.veqveq.cookbook.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.veqveq.cookbook.model.entity.Recipe;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {
    List<Recipe> findAllByCompletedIsNull();

    @Query("SELECT max(kcal) from Recipe")
    Integer getMaxKcal();

    @Query("SELECT max(proteins) from Recipe")
    Integer getMaxProteins();

    @Query("SELECT max(fats) from Recipe")
    Integer getMaxFats();

    @Query("SELECT max(carbons) from Recipe")
    Integer getMaxCarbons();

    @Query("SELECT max(cookTime) from Recipe")
    Double getMaxCookTime();

    @Query("SELECT max(portions) from Recipe")
    Integer getMaxPortions();
}
