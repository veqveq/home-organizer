package ru.veqveq.backend.model.enumerated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.Instant;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum DictionaryFieldType {
    Text("Строка", Function.identity()),
    Long("Целое число", java.lang.Long::parseLong),
    Double("Дробное число", java.lang.Double::parseDouble),
    Boolean("Логическое", java.lang.Boolean::parseBoolean),
    Date("Дата", Instant::parse);
    private final String name;
    private final Function<String, ?> mappingFunction;
}
