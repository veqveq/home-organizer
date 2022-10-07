package ru.veqveq.backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "dictionary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Dictionary {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "dictionary", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DictionaryField> fields = new HashSet<>();

    public void setFields(Set<DictionaryField> fields) {
        getFields().clear();
        if (Objects.isNull(fields)) {
            getFields().addAll(Collections.emptySet());
        } else {
            getFields().addAll(fields);
        }
    }
}
