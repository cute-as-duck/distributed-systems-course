package chat.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record Person(String username, String host, int port) {

    public static Person parseFromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, Person.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}
