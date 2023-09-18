package ru.veqveq.cookbook.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.veqveq.cookbook.model.entity.Type;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypeRepo extends JpaRepository<Type, UUID>, JpaSpecificationExecutor<Type> {
    Optional<Type> findByName(String name);
}
