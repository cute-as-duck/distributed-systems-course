package chat.config;

import chat.web.controller.ChatController;
import chat.web.ChatClient;
import chat.service.CallbackService;
import chat.service.EncryptionService;
import chat.frame.ChatFrame;
import chat.frame.LoginFrame;
import chat.service.MessageRouterService;
import chat.web.ResponseRouter;

public class BeanFactory {

    private static ChatFrame chatFrame;
    private static LoginFrame loginFrame;
    private static ChatClient chatClient;
    private static ChatController chatAPI;
    private static EncryptionService encryptionService;
    private static CallbackService callbackService;
    private static ResponseRouter responseRouter;
    private static MessageRouterService messageRouterService;

    private BeanFactory() {
    }

    public static synchronized ChatFrame chatFrame() {
        if (chatFrame == null) {
            chatFrame = new ChatFrame();
        }
        return chatFrame;
    }

    public static synchronized LoginFrame loginFrame() {
        if (loginFrame == null) {
            loginFrame = new LoginFrame();
        }
        return loginFrame;
    }

    public static synchronized ChatClient chatClient() {
        if (chatClient == null) {
            chatClient = new ChatClient();
        }
        return chatClient;
    }

    public static synchronized ChatController chatController() {
        if (chatAPI == null) {
            chatAPI = new ChatController();
        }
        return chatAPI;
    }

    public static synchronized EncryptionService encryptionService() {
        if (encryptionService == null) {
            encryptionService = new EncryptionService();
        }
        return encryptionService;
    }

    public static synchronized CallbackService callbackService() {
        if (callbackService == null) {
            callbackService = new CallbackService();
        }
        return callbackService;
    }

    public static synchronized ResponseRouter responseRouter() {
        if (responseRouter == null) {
            responseRouter = new ResponseRouter();
        }
        return responseRouter;
    }

    public static synchronized MessageRouterService messageRouterService() {
        if (messageRouterService == null) {
            messageRouterService = new MessageRouterService();
        }
        return messageRouterService;
    }
}
