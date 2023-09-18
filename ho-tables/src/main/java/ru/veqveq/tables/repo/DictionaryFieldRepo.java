package ru.veqveq.tables.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veqveq.tables.model.entity.DictionaryField;

import java.util.UUID;

@Repository
public interface DictionaryFieldRepo extends JpaRepository<DictionaryField, UUID> {
}
