package chat;

import chat.web.controller.ChatController;
import chat.web.ChatClient;
import chat.config.BeanFactory;
import chat.frame.LoginFrame;
import chat.service.EncryptionService;

import java.awt.*;

public class App{

    private static final String ENCRYPTION_KEY = "Encryption Key";

    private final ChatClient chatClient = BeanFactory.chatClient();
    private final EncryptionService encryptionService = BeanFactory.encryptionService();
    private final ChatController chatController = BeanFactory.chatController();
    private final LoginFrame loginFrame = BeanFactory.loginFrame();

    public static void main(String[] args) {
        new App().start();
    }

    private void start() {
        chatController.connectToServer(response -> encryptionService.setEncryptionKey(response.headers().get(ENCRYPTION_KEY)));

        EventQueue.invokeLater(() -> {
            loginFrame.setVisible(true);
        });

        chatClient.receiveMessage();
    }
}
