package server.filters;

import server.service.EncryptionService;
import server.model.Message;

public class EncryptingFilter implements Filter<Message, String> {

    private static final String ENCRYPTION_KEY = "Encryption Key";
    private static final String ENCRYPTED_DATA = "Encrypted Data";

    private final EncryptionService encryptionService = new EncryptionService();

    @Override
    public String process(Message message) {
        if (message.isEmpty()) {
            return "";
        } else if (message.headers().containsKey(ENCRYPTION_KEY)) {
            return message.toString();
        }
        return ENCRYPTED_DATA + "\n" + encryptionService.encrypt(message.toString());
    }
}
