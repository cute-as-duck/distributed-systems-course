package server.router;

import server.dto.AuthRequest;
import server.exception.UnknownRequestException;
import server.filters.DecryptingFilter;
import server.filters.EncryptingFilter;
import server.filters.Filter;
import server.handler.MessageHandler;
import server.model.Message;

public class MessageRouter{

    private static final String REQUEST_KEY = "Request";
    private static final String REQUEST_ID_KEY = "Request_id";
    private static final String CONNECT = "Connect to chat";
    private static final String SIGN_UP = "Sign Up";
    private static final String LOG_IN = "Log In";
    private static final String GET_PERSON_LIST = "Get Person List";
    private static final String EXIT_CHAT = "Exit Chat";

    private final MessageHandler handler = new MessageHandler();
    private final Filter<Message, String> encryptingFilter = new EncryptingFilter();
    private final Filter<String, String> decryptingFilter = new DecryptingFilter();

    public String route(String data) {
        String decryptedMessage = decryptingFilter.process(data);

        Message message = Message.parseMessage(decryptedMessage);

        String requestTask = message.headers().get(REQUEST_KEY);
        String requestId = message.headers().getOrDefault(REQUEST_ID_KEY, "-1");
        Message response = (
                switch (requestTask) {
                    case CONNECT -> handler.encryptionKey();
                    case SIGN_UP -> handler.addPerson(AuthRequest.fromJson(message.body()));
                    case LOG_IN -> handler.authenticate(AuthRequest.fromJson(message.body()));
                    case GET_PERSON_LIST -> handler.getOnlinePersons();
                    case EXIT_CHAT -> handler.exitChat(message.body());
                    default -> throw new UnknownRequestException();
                }
                );
        if (!requestId.equals("-1")) {
            response.addHeader(REQUEST_ID_KEY, requestId);
        }

        return encryptingFilter.process(response);
    }
}
