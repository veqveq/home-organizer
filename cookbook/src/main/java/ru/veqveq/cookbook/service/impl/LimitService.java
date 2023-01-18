package ru.veqveq.cookbook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.veqveq.cookbook.repo.RecipeRepo;

@Service
@RequiredArgsConstructor
public class LimitService{
    private final RecipeRepo recipeRepo;

    public Integer getMaxKcal(){
        return recipeRepo.getMaxKcal();
    }

    public Integer getMaxProteins(){
        return recipeRepo.getMaxProteins();
    }

    public Integer getMaxFats(){
        return recipeRepo.getMaxFats();
    }

    public Integer getMaxCarbons(){
        return recipeRepo.getMaxCarbons();
    }

    public Double getMaxCookTime(){
        return recipeRepo.getMaxCookTime();
    }

    public Integer getMaxPortions(){
        return recipeRepo.getMaxPortions();
    }
}
