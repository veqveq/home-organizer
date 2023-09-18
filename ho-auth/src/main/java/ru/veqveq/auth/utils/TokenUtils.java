package ru.veqveq.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class TokenUtils {
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();
    private static final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> decode(String token) {
        try {
            String[] chunks = token.split("\\.");
            String decoded = new String(decoder.decode(chunks[1]));
            return mapper.readValue(decoded, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getFieldFromToken(String token, String fieldName){
        return decode(token).get(fieldName);
    }
}
