package ru.veqveq.cookbook.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.veqveq.cookbook.model.entity.Category;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepo extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByName(String name);
}
