package ru.veqveq.cookbook.repo.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * Абстрактный класс для построения спецификаций
 *
 * @param <M> класс объекта, для которого строится спецификация
 * @param <F> класс фильтра, передаваемого для построения спецификации
 */
public abstract class AbstractSpecification<M, F> {
    /**
     * Метод возвращает пустую параметризованную нужным типом спецификацию (необходимо для построения цепочки вызовов
     * с использованием утилитарных параметризованных классов (CommonSpecification)
     *
     * @return спецификация
     */
    private Specification<M> getSpecification() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and();
    }

    /**
     * Метод возвращает спецификацию с необходимым набором фильтров
     *
     * @param filter фильтр
     * @return спецификация
     */
    public Specification<M> byFilter(F filter) {
        Specification<M> specification = getSpecification();
        return addFilters(specification, filter);
    }

    /**
     * Абстрактный метод добавления фильтров в спецификацию
     *
     * @param specification параметризованная спецификация
     * @param filter        фильтр
     * @return спецификация
     */
    protected abstract Specification<M> addFilters(Specification<M> specification, F filter);
}