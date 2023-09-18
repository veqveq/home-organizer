package ru.veqveq.tables.model;

import lombok.Data;

import java.util.Map;

@Data
public class DictionaryItemFilter {
    private String commonFilter;
    private Map<String, Object> fieldFilters;
}
