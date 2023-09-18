package ru.veqveq.cookbook.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.veqveq.cookbook.model.entity.IngredientName;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngredientNameRepo extends JpaRepository<IngredientName, UUID>, JpaSpecificationExecutor<IngredientName> {
    Optional<IngredientName> findByName(String name);

    Page<IngredientName> findAllByGroupIngredientNameIsNull(Pageable pageable);
}
