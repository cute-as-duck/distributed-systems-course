package chat.filter;

import chat.config.BeanFactory;
import chat.service.EncryptionService;

public class DecryptingFilter{

    private static final String ENCRYPTED_DATA = "Encrypted Data";

    private final EncryptionService encryptionService = BeanFactory.encryptionService();

    public String process(String data) {
        String[] tokens = data.split("\n", 2);
        if (tokens[0].equals(ENCRYPTED_DATA)) {
            return encryptionService.decrypt(tokens[1]);
        } else {
            return data;
        }
    }
}
