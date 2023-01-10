package ru.veqveq.cookbook.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ru.veqveq.cookbook.model.ValueInterval;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Collection;
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
     * Пример: объект Order содержит внутри себя список объектов ObjectCard и внутри есть поле status.
     * В качестве проверяемых значений передадут несколько статусов [ObjectStatus.DRAFT, ObjectStatus.ERROR]
     * в результате будет фильтрация по статусам.
     *
     * @param joinObjectFieldName имя поля внутри корневого объекта, ссылающегося на объект/таблицу с которой будет происходить join
     * @param fieldName           имя поля которое будет учавствовать в поиске по вхождению в массив значений
     * @param values              список значений которое может принимать поле
     * @param <T>                 тип с которым работает спецификация
     * @param <R>                 тип данных в коллекции
     * @return типизированную спецификацию
     */
    public <T, R> Specification<T> searchInJoinedObjectIn(
            String joinObjectFieldName,
            String fieldName,
            Collection<R> values
    ) {
        return isEmpty(values) ? null : (root, query, criteriaBuilder) -> {
            Join<T, Object> objectCards = root.join(joinObjectFieldName);
            return objectCards.get(fieldName).in(values);
        };
    }

    /**
     * Предоставляет возможность поиска по равенству числового значения во внутреннем объекте,
     * пользуя двойной join. Тип INNER
     * Пример : объект Order содержит внутри себя список объектов ObjectCard, который содержит объект CopyrightHolder,
     * внутри которого есть поле copyrightHolderId.
     * В качестве значения для поиска будет передан copyrightHolderId. В результате будет фильтрация по равенству числового значения
     *
     * @param joinObjectFieldName1 имя поля внутри корневого объекта, ссылающегося на объект/таблицу с которой будет происходить join
     * @param joinObjectFieldName2 имя поля внутри второго объекта, ссылающегося на объект/таблицу с которой будет происходить join
     * @param fieldName            имя поля которое будет участвовать для сравнения по частичному вхождению
     * @param values               список значений которое может принимать поле
     * @param <T>                  тип объекта с которым работает спецификация
     * @param <R>                  тип данных в коллекции
     * @return типизированную спецификацию
     */
    public <T, R> Specification<T> searchWithDoubleJoinCollectionFullIn(
            String joinObjectFieldName1,
            String joinObjectFieldName2,
            String fieldName,
            Collection<R> values
    ) {
        return isEmpty(values) ? null : (root, query, cb) -> {
            Join<Object, Object> joinedEntry = root.join(joinObjectFieldName1, JoinType.INNER);
            Join<Object, Object> joinedEntry2 = joinedEntry.join(joinObjectFieldName2, JoinType.INNER);
            Predicate predicate = cb.conjunction();
            predicate.getExpressions().add(joinedEntry2.get(fieldName).in(values));
            query.groupBy(root.get("id"));
            query.having(cb.equal(cb.count(joinedEntry2.get(fieldName)), values.size()));
            return predicate;
        };
    }

    public <T, R> Specification<T> searchNotIn(String fieldName, Collection<R> excludes) {
        return isEmpty(excludes) ? null : (root, query, cb) -> cb.not(root.get(fieldName).in(excludes));
    }
}