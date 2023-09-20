package ru.veqveq.tables.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import ru.veqveq.tables.model.AuditorAwareEntity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "dictionary")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ToString(callSuper = true)
public class Dictionary extends AuditorAwareEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "es_index_name")
    @GeneratedValue
    private UUID esIndexName;

    @OneToMany(mappedBy = "dictionary", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DictionaryField> fields = new ArrayList<>();

    public void setFields(Set<DictionaryField> fields) {
        getFields().clear();
        if (Objects.isNull(fields)) {
            getFields().addAll(Collections.emptySet());
        } else {
            getFields().addAll(fields);
        }
    }

    public String getEsIndexName() {
        return esIndexName.toString();
    }
}
