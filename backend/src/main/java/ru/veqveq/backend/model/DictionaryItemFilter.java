package ru.veqveq.backend.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DictionaryItemFilter {
    private List<String> commonFilters;
    private Map<String, Object> fieldFilters;
}
