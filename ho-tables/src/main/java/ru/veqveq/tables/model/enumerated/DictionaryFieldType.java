package ru.veqveq.tables.model.enumerated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum DictionaryFieldType {
    Text("Строка", Function.identity()),
    Long("Целое число", java.lang.Long::parseLong),
    Double("Дробное число", java.lang.Double::parseDouble),
    Boolean("Логическое", val -> {
        val = val.trim();
        if (val.equalsIgnoreCase("true")) {
            return true;
        } else if (val.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new RuntimeException();
        }
    }),
    Date("Дата", LocalDate::parse);
    private final String name;
    private final Function<String, ?> mappingFunction;
}
