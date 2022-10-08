package ru.veqveq.backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "dictionary_field")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryField {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DictionaryFieldType type;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "unique_value")
    private Boolean uniqueValue;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;
}
