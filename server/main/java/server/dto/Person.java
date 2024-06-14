package server.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.model.PersonDetails;

public record Person(String username, String host, int port) {

    public static Person fromPersonDetails(PersonDetails personDetails) {
        return new Person(personDetails.username(), personDetails.host(), personDetails.port());
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}
