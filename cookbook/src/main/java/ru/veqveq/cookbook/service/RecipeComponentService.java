package ru.veqveq.cookbook.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeComponentService<D, F> {
    Page<D> getAll(Pageable pageable);

    Page<D> filter(F filter, Pageable pageable);
}
