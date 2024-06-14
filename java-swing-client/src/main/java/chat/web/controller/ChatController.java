package chat.web.controller;

import chat.config.AppProps;
import chat.dto.AuthRequest;
import chat.dto.ChatMessage;
import chat.dto.Person;
import chat.dto.SendingFile;
import chat.service.CallbackService;
import chat.web.ChatClient;
import chat.web.model.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChatController {

    private static Map<String, InetAddress> ipAddresses = new HashMap<>();

    private static final String SERVER_HOST_NAME = AppProps.serverHost();
    private static final int SERVER_PORT = AppProps.serverPort();
    private static InetAddress serverInetAddress;

    private static final String SELF_HOST_NAME = AppProps.selfHost();
    private static final int SELF_PORT = AppProps.selfPort();

    private static final String REQUEST_KEY = "Request";
    private static final String MESSAGE_KEY = "Message";
    private static final String REQUEST_ID_KEY = "Request_id";

    private static final String CONNECT = "Connect to chat";
    private static final String SIGN_UP = "Sign Up";
    private static final String LOG_IN = "Log In";
    private static final String CHAT_MESSAGE = "Chat Message";
    private static final String GET_PERSON_LIST = "Get Person List";
    private static final String EXIT_CHAT = "Exit Chat";
    private static final String FILE = "File";


    private final ChatClient chatClient = new ChatClient();
    private final CallbackService callbackService = new CallbackService();

    static {
        try {
            serverInetAddress = InetAddress.getByName(SERVER_HOST_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer(Consumer<Message> callback) {
        Message message = new Message()
                .addHeader(REQUEST_KEY, CONNECT)
                .addHeader(REQUEST_ID_KEY, callbackService.registerRequest(callback));


        chatClient.sendMessage(message, serverInetAddress, SERVER_PORT);
    }

    public void register(String username, String password, Consumer<Message> callback) {
        Message message = new Message()
                .addHeader(REQUEST_KEY, SIGN_UP)
                .addHeader(REQUEST_ID_KEY, callbackService.registerRequest(callback))
                .addBody(new AuthRequest(username, password, SELF_HOST_NAME, SELF_PORT).toJson());

        chatClient.sendMessage(message, serverInetAddress, SERVER_PORT);
    }

    public void logIn(String username, String password, Consumer<Message> callback) {
        Message message = new Message()
                .addHeader(REQUEST_KEY, LOG_IN)
                .addHeader(REQUEST_ID_KEY, callbackService.registerRequest(callback))
                .addBody(new AuthRequest(username, password, SELF_HOST_NAME, SELF_PORT).toJson());

        chatClient.sendMessage(message, serverInetAddress, SERVER_PORT);
    }

    public void exitChat(String username) {
        Message message = new Message()
                .addHeader(REQUEST_KEY, EXIT_CHAT)
                .addBody(username);

        chatClient.sendMessage(message, serverInetAddress, SERVER_PORT);
    }

    public void getPersonList(Consumer<Message> callback) {
        Message message = new Message()
                .addHeader(REQUEST_KEY, GET_PERSON_LIST)
                .addHeader(REQUEST_ID_KEY, callbackService.registerRequest(callback));

        sendToServer(message);
    }

    public void sendMessage(Person person, ChatMessage chatMessage) {
        Message message = new Message()
                .addHeader(MESSAGE_KEY, CHAT_MESSAGE)
                .addBody(chatMessage.toJson());
        chatClient.sendMessage(message, getInetAddressByName(person.host()), person.port());
    }

    public void sendFile(Person person, String fileName, byte[] content) {
        Message message = new Message()
                .addHeader(MESSAGE_KEY, FILE)
                .addBody(new SendingFile(person.username(), fileName, new String(content, StandardCharsets.UTF_8)).toJson());

        chatClient.sendMessage(message, getInetAddressByName(person.host()), person.port());
    }

    private static InetAddress getInetAddressByName(String host) {
        InetAddress inetAddress;
        if (!ipAddresses.containsKey(host)) {
            try {
                inetAddress = InetAddress.getByName(host);
                ipAddresses.put(host, inetAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return ipAddresses.get(host);
    }

    private void sendToServer(Message message) {
        chatClient.sendMessage(message, serverInetAddress, SERVER_PORT);
    }
}
