package ru.veqveq.cookbook.service;

public interface Parser {
    void parseRegister();

    void parsePages();

    Integer deleteDuplicates();

    void optimizeIngredientNames();
}
