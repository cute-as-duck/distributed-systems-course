package chat.service;

import chat.web.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CallbackService {
    private static final String REQUEST_ID_KEY = "Request_id";
    private int requestIdInc = 1;

    private static Map<Integer, Consumer<Message>> callbacks = new HashMap<>();

    public String registerRequest(Consumer<Message> consumer) {
        final int requestId = requestIdInc++;
        callbacks.put(requestId, consumer);
        return String.valueOf(requestId);
    }

    public void handleResponse(Message message) {
        int requestId = Integer.parseInt(message.headers().get(REQUEST_ID_KEY));
        callbacks.remove(requestId).accept(message);
    }
}
