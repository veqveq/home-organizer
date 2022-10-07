package ru.veqveq.backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import ru.veqveq.backend.model.constants.ElasticConstants;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Document(indexName = ElasticConstants.DICTIONARY_INDEX_NAME)
public class DictionaryItem {
    @Id
    private String id;

    @Field(type = FieldType.Text, fielddata = true)
    private String item;

    @Field(type = FieldType.Text)
    private String description;
}
