package server.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record AuthRequest(String host, int port, String username, String password) {
    public static AuthRequest fromJson(String body) {
        try {
            return new ObjectMapper().readValue(body, AuthRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
