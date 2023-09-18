package ru.veqveq.cookbook.model;

import lombok.Getter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Objects;

@Getter
public class ValueInterval<V extends Comparable<V>> {
    private V from;
    private V to;

    private boolean isEmpty() {
        return Objects.isNull(from) && Objects.isNull(to);
    }

    public Predicate getPredicate(CriteriaBuilder cb, Expression<V> extension) {
        if (isEmpty()) {
            return null;
        } else if (Objects.isNull(from)) {
            return cb.lessThanOrEqualTo(extension, this.to);
        } else if (Objects.isNull(to)) {
            return cb.greaterThanOrEqualTo(extension, this.from);
        } else {
            return cb.between(extension, this.from, this.to);
        }
    }
}
