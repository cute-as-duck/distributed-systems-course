package server.filters;

import server.service.EncryptionService;

public class DecryptingFilter implements Filter<String, String> {

    private static final String CONTENT = "Content";
    private static final String ENCRYPTED_DATA = "Encrypted Data";

    private final EncryptionService encryptionService = new EncryptionService();

    @Override
    public String process(String data) {
        String[] tokens = data.split("\n", 2);
        if (tokens[0].equals(ENCRYPTED_DATA)) {
            return encryptionService.decrypt(tokens[1]);
        } else {
            return data;
        }
    }
}
