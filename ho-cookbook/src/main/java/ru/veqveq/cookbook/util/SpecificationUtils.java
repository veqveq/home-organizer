package ru.veqveq.cookbook.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.veqveq.cookbook.model.ValueInterval;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.springframework.util.CollectionUtils.isEmpty;

@UtilityClass
public final class SpecificationUtils {
    /**
     * Поиск на частичное совпадение поля таблицы и значения типа String. Регистронезависимый поиск
     *
     * @param fieldName имя поля в котором будет производиться поиск
     * @param value     значение для которого ищется строгое совпадение
     * @return спецификация
     */
    public <T> Specification<T> searchLike(String fieldName, String value) {
        return StringUtils.isEmpty(value)
                ? null
                : (root, query, cb) ->
                cb.and(Stream.of(value.split("\\s"))
                        .map(token -> cb.like(cb.lower(root.get(fieldName)), "%" + token.trim().toLowerCase() + "%"))
                        .toArray(Predicate[]::new)
                );
    }

    /**
     * Поиск на вхождение значения таблицы в заданный интервал в интервал
     *
     * @param fieldName имя поля по которому будет производиться поиск
     * @param interval  интервал внутри которого ищется вхождение
     * @return спецификация
     */
    public <T> Specification<T> searchInInterval(String fieldName, ValueInterval<?> interval) {
        return Objects.isNull(interval)
                ? null
                : ((root, query, criteriaBuilder) -> interval.getPredicate(criteriaBuilder, root.get(fieldName)));
    }

    /**
     * Предоставляет возможность поиска вхождения в массиве значений по полю внутри вложенного объекта. Тип INNER.
     * Пример: объект Recipe содержит внутри себя список объектов Ingredient и внутри есть поле IngredientName и внутри есть поле id.
     * В качестве проверяемых значений передадут несколько ID [1,2,3]
     * в результате будет фильтрация рецептов по вхождению идентификаторов названий ингредиентов в список.
     *
     * @param joinObjectFieldNames путь до поля fieldName (от корневой сущности к целевой)
     * @param fieldName            имя поля которое будет участвовать в поиске по вхождению в массив значений
     * @param values               список значений которое может принимать поле
     * @param <T>                  тип с которым работает спецификация
     * @param <R>                  тип данных в коллекции
     * @return типизированную спецификацию
     */
    public <T, R> Specification<T> searchInJoinedObjectIn(
            List<String> joinObjectFieldNames,
            String fieldName,
            Collection<R> values
    ) {
        return isEmpty(values) ? null : (root, query, criteriaBuilder) -> {
            Join<Object, Object> joinedEntry = null;
            for (String joinedObjFldName : joinObjectFieldNames) {
                if (Objects.isNull(joinedEntry)) {
                    joinedEntry = root.join(joinedObjFldName, JoinType.INNER);
                } else {
                    joinedEntry = joinedEntry.join(joinedObjFldName, JoinType.INNER);
                }
            }
            if (Objects.isNull(joinedEntry)) {
                return null;
            }
            return joinedEntry.get(fieldName).in(values);
        };
    }

    public <T, R> Specification<T> searchNotIn(String fieldName, Collection<R> excludes) {
        return isEmpty(excludes) ? null : (root, query, cb) -> cb.not(root.get(fieldName).in(excludes));
    }

    public <T> Specification<T> searchNull(String fieldName) {
        return (root, query, cb) -> cb.isNull(root.get(fieldName));
    }
}