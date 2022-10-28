package ru.veqveq.backend.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.veqveq.backend.model.AuditorAwareEntity;
import ru.veqveq.backend.model.enumerated.DictionaryFieldType;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "dictionary_field")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class DictionaryField extends AuditorAwareEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DictionaryFieldType type;

    @Column(name = "position")
    private Integer position;

    @Column(name = "unique_value")
    private Boolean unique;

    @Column(name = "required_value")
    private Boolean required;

    @Column(name = "default_value")
    private String defaultValue;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;
}
