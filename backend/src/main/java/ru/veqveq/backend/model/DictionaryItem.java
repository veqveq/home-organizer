package ru.veqveq.backend.model;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Map;

@Data
@FieldNameConstants
public class DictionaryItem {
    private String id;

    private Map<String,Object> fieldValues;
}
