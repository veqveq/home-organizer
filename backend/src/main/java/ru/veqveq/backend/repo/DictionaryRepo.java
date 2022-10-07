package ru.veqveq.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veqveq.backend.model.entity.Dictionary;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DictionaryRepo extends JpaRepository<Dictionary, UUID> {
    boolean existsByName(String name);

    Optional<Dictionary> findByName(String name);

}
