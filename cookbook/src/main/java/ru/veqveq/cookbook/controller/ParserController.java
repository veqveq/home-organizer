package ru.veqveq.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veqveq.cookbook.service.Parser;

@Tag(name = "Контроллер парсинга")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parser")
public class ParserController {
    private final Parser parser;

    @PostMapping
    @Operation(summary = "Парсинг реестра рецептов")
    public void parseRegister() {
        parser.parseRegister();
    }

    @PostMapping("/pages")
    @Operation(summary = "Парсинг страниц рецептов")
    public void parseProductPages() {
        parser.parsePages();
    }

    @PostMapping("/delete-duples")
    @Operation(summary = "Удаление повторяющихся рецептов")
    public ResponseEntity<String> deleteDuples() {
        return ResponseEntity.ok(String.format("Удалено %d дубликатов", parser.deleteDuplicates()));
    }
}
