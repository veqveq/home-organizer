package ru.veqveq.cookbook.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.veqveq.cookbook.config.properties.ParserProperties;
import ru.veqveq.cookbook.model.entity.*;
import ru.veqveq.cookbook.repo.*;
import ru.veqveq.cookbook.service.Parser;
import ru.veqveq.cookbook.util.ParserUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParserImpl implements Parser {
    private static Integer PARSED_ITEMS = 0;
    private final ParserProperties properties;
    private final CategoryRepo categoryRepo;
    private final TypeRepo typeRepo;
    private final IngredientNameRepo ingredientNameRepo;
    private final IngredientUnitRepo ingredientUnitRepo;
    private final KitchenRepo kitchenRepo;
    private final RecipeRepo recipeRepo;

    private final IngredientRepo ingredientRepo;


    @Override
    public void parseRegister() {
        Boolean isFinish = false;
        int startPage = 1;
        while (!isFinish) {
            try {
                Document page = getPage(properties.getRegisterFragment(), startPage);
                isFinish = parseRegisterPage(page);
                startPage += 1;
            } catch (IOException e) {
                log.error("Disconnect. Page: {}", startPage);
            }
        }
    }

    @Override
    public void parsePages() {
        List<Recipe> recipes = recipeRepo.findAllByCompletedIsNull();
        int counter = 0;
        for (Recipe recipe : recipes) {
            parseRecipePage(recipe);
            counter += 1;
            log.info("Обработано рецептов: {}", counter);
        }
    }

    @Override
    public Integer deleteDuplicates() {
        int duplicates = 0;
        List<Recipe> recipes = recipeRepo.findAll();
        Set<String> uniqueUrl = new HashSet<>();
        for (Recipe recipe : recipes) {
            if (uniqueUrl.contains(recipe.getUrl())) {
                recipeRepo.deleteById(recipe.getId());
                duplicates +=1;
            } else {
                uniqueUrl.add(recipe.getUrl());
            }
        }
        return duplicates;
    }

    public void parseRecipePage(Recipe recipe) {
        Document page = getPage(recipe.getUrl());
        Elements recipePageData = page.getElementsByTag("main");
        recipe.setKcal(ParserUtils.parseKcal(recipePageData));
        recipe.setProteins(ParserUtils.parseProteins(recipePageData));
        recipe.setFats(ParserUtils.parseFats(recipePageData));
        recipe.setCarbons(ParserUtils.parseCarbons(recipePageData));
        recipe.setDescription(ParserUtils.parseDescription(recipePageData));
        recipe.setCategory(parseCategory(recipePageData));
        parseIngredients(recipePageData, recipe);
        recipe.setCompleted(true);
        recipeRepo.saveAndFlush(recipe);
    }

    public Boolean parseRegisterPage(Document page) {
        Elements recipes = page.getElementsContainingOwnText("Добавить в книгу рецептов");
        if (recipes.isEmpty()) {
            return true;
        }
        for (Element recipeRegisterData : recipes) {
            String pageEndpoint = ParserUtils.parseRecipePageLink(recipeRegisterData);

            Recipe recipe = Recipe.builder()
                    .title(ParserUtils.parseTitle(recipeRegisterData))
                    .portions(ParserUtils.parsePortion(recipeRegisterData))
                    .cookTime(ParserUtils.parseCookTime(recipeRegisterData))
                    .likes(ParserUtils.parseLikes(recipeRegisterData))
                    .dislikes(ParserUtils.parseDislikes(recipeRegisterData))
                    .imageUrl(properties.getStartUrl() + ParserUtils.parseImageLink(recipeRegisterData))
                    .url(properties.getStartUrl() + pageEndpoint)
                    .type(parseType(recipeRegisterData))
                    .kitchen(parseKitchen(recipeRegisterData))
                    .build();
            PARSED_ITEMS += 1;
            recipeRepo.save(recipe);
            log.info("Обработано: {}", PARSED_ITEMS);
        }
        return false;
    }

    private Type parseType(Element element) {
        String typeName = ParserUtils.parseTypeName(element);
        if (StringUtils.isBlank(typeName)) {
            return null;
        }
        return typeRepo.findByName(typeName).orElseGet(() -> typeRepo.saveAndFlush(new Type(typeName)));
    }

    private Category parseCategory(Elements element) {
        String categoryName = ParserUtils.parseCategoryName(element);
        if (StringUtils.isBlank(categoryName)) {
            return null;
        }
        return categoryRepo.findByName(categoryName).orElseGet(() -> categoryRepo.saveAndFlush(new Category(categoryName)));
    }

    private Kitchen parseKitchen(Element element) {
        String kitchenName = ParserUtils.parseKitchenName(element);
        if (StringUtils.isBlank(kitchenName)) {
            return null;
        }
        return kitchenRepo.findByName(kitchenName).orElseGet(() -> kitchenRepo.saveAndFlush(new Kitchen(kitchenName)));
    }

    private List<Ingredient> parseIngredients(Elements elements, Recipe recipe) {
        List<Ingredient> ingredients = new ArrayList<>();
        List<Element> ingredientElements = ParserUtils.getIngredientsData(elements);
        for (int i = 1; i < ingredientElements.size(); i++) {
            List<TextNode> nodes = ingredientElements.get(i).getElementsByTag("span").textNodes();
            String name = nodes.get(0).text();
            IngredientName ingredientName = ingredientNameRepo.findByName(name).orElseGet(() -> ingredientNameRepo.saveAndFlush(new IngredientName(name)));
            String capacity = nodes.get(1).text();
            Double count = null;
            String unitName;
            if (capacity.matches("[\\d,]+ \\D+")) {
                String[] splited = capacity.split("\\s");
                count = Double.parseDouble(splited[0].replace(",", "."));
                List<String> unitParts = Arrays.stream(splited).skip(1).collect(Collectors.toList());
                unitName = StringUtils.join(unitParts, " ").trim();
            } else {
                unitName = capacity;
            }
            IngredientUnit unit = ingredientUnitRepo.findByName(unitName).orElseGet(() -> ingredientUnitRepo.saveAndFlush(new IngredientUnit(unitName)));
            Ingredient ingredient = Ingredient.builder()
                    .recipe(recipe)
                    .name(ingredientName)
                    .count(count)
                    .unit(unit)
                    .build();
            ingredients.add(ingredient);
        }

        return ingredientRepo.saveAllAndFlush(ingredients);
    }

    private Document getPage(String endpoint, Integer pageNumber) throws IOException {
        if (endpoint.startsWith("/")) {
            endpoint = endpoint.substring(1);
        }
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(properties.getStartUrl())
                .pathSegment(endpoint);
        if (Objects.nonNull(pageNumber)) {
            builder.queryParam(properties.getPaginationParam(), pageNumber);
        }
        String url = builder.build().toString();
        return Jsoup.connect(url).get();
    }

    private Document getPage(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("Reconnect...");
            return getPage(url);
        }
    }
}
