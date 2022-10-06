package ru.veqveq.backend.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.veqveq.backend.model.entity.DictionaryItem;

import java.util.Optional;

@Repository
public interface DictionaryItemRepo extends ElasticsearchRepository<DictionaryItem, String> {
    Page<DictionaryItem> findByItemLikeOrDescriptionLike(String item, String description, Pageable pageable);

    Optional<DictionaryItem> findByItem(String item);
}
