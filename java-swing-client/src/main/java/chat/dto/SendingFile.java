package chat.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record SendingFile(String senderName, String fileName, String content) {

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static SendingFile fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, SendingFile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new SendingFile("", "", "");
    }
}
