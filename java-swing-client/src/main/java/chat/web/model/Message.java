package chat.web.model;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private Map<String, String> headers = new HashMap<>();
    private String body = "";

    public Message() {
    }

    public Message(Map<String, String> headers, String body) {
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> headers() {
        return this.headers;
    }

    public String body() {
        return this.body;
    }

    public Message addBody(String body) {
        this.body = body;
        return this;
    }

    public Message addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    private String headersToString() {
        StringBuilder headerLines = new StringBuilder();
        for(Map.Entry<String, String> entry : this.headers.entrySet()) {
            headerLines.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return headerLines.toString();
    }

    public String toJson() {
        return headersToString() + "\n" + this.body;
    }

    private static Map<String, String> parseHeaders(String headerLines) {
        String[] lines = headerLines.split("\n");
        Map<String, String> headers = new HashMap<>();
        for (String header : lines) {
            String[] elements = header.split(":");
            headers.put(elements[0], elements[1].trim());
        }
        return headers;
    }

    public static Message parseMessage(String data) {
        String[] tokens = data.split("\n{2}", 2);
        if (tokens.length == 2) {

            return new Message(parseHeaders(tokens[0]), tokens[1]);
        } else if (tokens.length == 1) {
            return new Message(parseHeaders(tokens[0]), "");
        } else throw new RuntimeException();
    }
}
