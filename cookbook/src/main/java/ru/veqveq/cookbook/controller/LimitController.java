package ru.veqveq.cookbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veqveq.cookbook.service.impl.LimitService;

@Tag(name = "Контроллер лимитов")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/limits")
public class LimitController {
    private final LimitService service;

    @GetMapping("/kcal")
    @Operation(summary = "Получить максимальное значение калорийности")
    public Integer getMaxKcal(){
        return service.getMaxKcal();
    }

    @GetMapping("/proteins")
    @Operation(summary = "Получить максимальное значение белков")
    public Integer getMaxProteins(){
        return service.getMaxProteins();
    }

    @GetMapping("/fats")
    @Operation(summary = "Получить максимальное значение жиров")
    public Integer getMaxFats(){
        return service.getMaxFats();
    }

    @GetMapping("/carbons")
    @Operation(summary = "Получить максимальное значение углеводов")
    public Integer getMaxCarbons(){
        return service.getMaxCarbons();
    }

    @GetMapping("/cooktime")
    @Operation(summary = "Получить максимальное значение времени готовки")
    public Double getMaxCookTime(){
        return service.getMaxCookTime();
    }

    @GetMapping("/portions")
    @Operation(summary = "Получить максимальное значение кол-ва порций")
    public Integer getMaxPortions(){
        return service.getMaxPortions();
    }
}
