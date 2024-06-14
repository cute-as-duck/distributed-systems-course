package chat.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record PersonListResponse(List<Person> persons) {

    public static PersonListResponse fromJson(String body) {
        try {
            return new ObjectMapper().readValue(body, PersonListResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
