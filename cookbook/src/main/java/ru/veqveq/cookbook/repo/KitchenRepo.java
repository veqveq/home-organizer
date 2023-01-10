package ru.veqveq.cookbook.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.veqveq.cookbook.model.entity.Kitchen;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KitchenRepo extends JpaRepository<Kitchen, UUID>, JpaSpecificationExecutor<Kitchen> {
    Optional<Kitchen> findByName(String name);
}
