package ru.veqveq.cookbook.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class ParserUtils {
    public Integer parseLikes(Element element) {
        String likes = null;
        try {
            likes = element.parent().parent().parent().parent().parent().child(1).child(0).child(1).text();
            return Integer.parseInt(likes);
        } catch (NumberFormatException e) {
            log.info("Лайки не число {}: {}", likes, element);
            return 0;
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга лайков: {}", element);
            return null;
        }
    }

    public Integer parseDislikes(Element element) {
        String dislikes = null;
        try {
            dislikes = element.parent().parent().parent().parent().parent().child(1).child(0).child(2).text();
            return Integer.parseInt(dislikes);
        } catch (NumberFormatException e) {
            log.info("Дизлайки не число {}: {}", dislikes, element);
            return 0;
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга дизлайков: {}", element);
            return null;
        }
    }

    public String parseTitle(Element element) {
        try {
            return element.parent().parent().parent().parent().parent().parent().parent().parent().parent().child(1).text();
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга названия: {}", element);
            return null;
        }
    }

    public Integer parsePortion(Element element) {
        String portions = null;
        try {
            portions = element.parent().parent().parent().parent().parent().parent().parent().parent().parent().child(2).child(1).child(0).child(1).text().split("\\s")[0];
            return Integer.parseInt(portions);
        } catch (NumberFormatException e) {
            log.info("Порции не число {}: {}", portions, element);
            return 0;
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга кол-ва порций: {}", element);
            return null;
        }
    }

    public Double parseCookTime(Element element) {
        String[] cookTime = null;
        double time = 0;
        try {
            cookTime = element.parent().parent().parent().parent().parent().parent().parent().parent().parent().child(2).child(1).child(0).child(2).text().split("\\s");
            time = Double.parseDouble(cookTime[0]);
            if (cookTime[1].toLowerCase().contains("секунд")) {
                time /= 60;
            } else if (cookTime[1].toLowerCase().contains("час")) {
                time *= 60;
                if(cookTime.length == 4){
                    time += Double.parseDouble(cookTime[2]);
                }
            } else if (cookTime[1].toLowerCase().contains("минут")){
                if(cookTime.length == 4){
                    time += Double.parseDouble(cookTime[2])/60;
                }
            }
            return time;
        } catch (NumberFormatException e) {
            log.info("Время не число {}: {}", time, element);
            return 0.0;
        } catch (NullPointerException e){
            log.error("Ошибка парсинга времени готовки: {}", element);
            return null;
        }
    }

    public String parseRecipePageLink(Element element){
        try {
            return element.parent().parent().parent().parent().parent().parent().parent().parent().parent().child(1).tagName("a").child(0).attr("href");
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга ссылки на страницу рецепт: {}", element);
            return null;
        }
    }

    public String parseImageLink(Element element){
        try {
            return element.parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().child(0).getElementsByTag("img").get(0).attr("src");
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга ссылки на изображение: {}", element);
            return null;
        }
    }

    public String parseDescription(Elements element){
        try {
            return element.get(0).child(0).child(0).child(0).child(0).child(9).child(0).child(2).text();
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга описания: {}", element);
            return null;
        }
    }

    public Integer parseKcal(Elements element) {
        String kcal = null;
        try {
            kcal = element.get(0).child(0).child(0).child(0).child(0).child(9).child(0).getElementsContainingText("Энергетическая ценность на порцию").get(1).child(2).child(0).child(0).text();
            return Integer.parseInt(kcal);
        } catch (NumberFormatException e) {
            log.info("Калории не число {}: {}", kcal, element);
            return 0;
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга калорий: {}", element);
            return null;
        }
    }

    public Integer parseProteins(Elements element) {
        String proteins = null;
        try {
            proteins = element.get(0).child(0).child(0).child(0).child(0).child(9).child(0).getElementsContainingText("Энергетическая ценность на порцию").get(1).child(2).child(0).child(1).text();
            return Integer.parseInt(proteins);
        } catch (NumberFormatException e) {
            log.info("Белки не число {}: {}", proteins, element);
            return 0;
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга белков: {}", element);
            return null;
        }
    }

    public Integer parseFats(Elements element) {
        String fats = null;
        try {
            fats = element.get(0).child(0).child(0).child(0).child(0).child(9).child(0).getElementsContainingText("Энергетическая ценность на порцию").get(1).child(2).child(0).child(2).text();
            return Integer.parseInt(fats);
        } catch (NumberFormatException e) {
            log.info("Жиры не число {}: {}", fats, element);
            return 0;
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга жиров: {}", element);
            return null;
        }
    }

    public Integer parseCarbons(Elements element) {
        String carbons = null;
        try {
            carbons = element.get(0).child(0).child(0).child(0).child(0).child(9).child(0).getElementsContainingText("Энергетическая ценность на порцию").get(1).child(2).child(0).child(3).text();
            return Integer.parseInt(carbons);
        } catch (NumberFormatException e) {
            log.info("Углеводы не число {}: {}", carbons, element);
            return 0;
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга углеводов: {}", element);
            return null;
        }
    }

    public String parseTypeName(Element element){
        try {
            return element.parent().parent().parent().parent().parent().parent().parent().parent().parent().child(0).tagName("span").child(0).text();
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга типа блюда: {}", element);
            return null;
        }
    }

    public String parseKitchenName(Element element){
        try {
            return element.parent().parent().parent().parent().parent().parent().parent().parent().parent().child(0).tagName("span").child(2).text();
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга кухни: {}", element);
            return null;
        } catch (IndexOutOfBoundsException e) {
            return "Кухня не указана";
        }
    }

    public String parseCategoryName(Elements element){
        try {
            return element.get(0).child(0).child(0).child(0).child(0).child(8).lastElementChild().getElementsByTag("nav").last().getElementsByTag("span").last().text();
        } catch (NullPointerException e) {
            log.error("Ошибка парсинга категории: {}", element);
            return null;
        }
    }

    public List<Element> getIngredientsData(Elements element){
        try {
            return element.get(0).child(0).child(0).child(0).child(0).child(9).child(0).getElementsByAttributeValue("itemprop","recipeIngredient").stream().map(el->el.parent().parent().parent()).collect(Collectors.toList());
        }catch (NullPointerException e) {
            log.error("Ошибка получения списка ингредиентов: {}", element);
            return null;
        }
    }

}
