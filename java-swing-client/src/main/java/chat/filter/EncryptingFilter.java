package chat.filter;

import chat.config.BeanFactory;
import chat.service.EncryptionService;
import chat.web.model.Message;

public class EncryptingFilter{

    private static final String CONNECT = "Connect to chat";
    private static final String ENCRYPTED_DATA = "Encrypted Data";

    private final EncryptionService encryptionService = BeanFactory.encryptionService();

    public String process(Message message) {
        if (!message.headers().containsValue(CONNECT)) {
            return ENCRYPTED_DATA + "\n" + encryptionService.encrypt(message.toJson());
        }
        return message.toJson();
    }
}
