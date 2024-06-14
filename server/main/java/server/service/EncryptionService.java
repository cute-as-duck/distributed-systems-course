package server.service;

import server.config.AppProps;
import server.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EncryptionService {

    private static final String PROP_KEY = "encryption.key";

    private static String symbols;
    private static String key;

    static {
        IntStream digits = IntStream.rangeClosed('0', '9');
        IntStream az = IntStream.rangeClosed('a', 'z');
        IntStream azCups = IntStream.rangeClosed('A', 'Z');

        IntStream part1 = IntStream.concat(digits, az);
        IntStream out = IntStream.concat(part1, azCups);

        symbols = out.mapToObj(c -> String.valueOf((char)c)).collect(Collectors.joining());
        key = AppProps.getProp(PROP_KEY);
    }

    public Message encryptMessage(Message message) {
        String encryptedBody = encrypt(message.body());

        Map<String, String> encryptedMap = new HashMap<>();
        for (Map.Entry<String,String> entry : message.headers().entrySet()) {
            String encryptedKey = encrypt(entry.getKey());
            String encryptedValue = encrypt(entry.getValue());
            encryptedMap.put(encryptedKey, encryptedValue);
        }

        return new Message(encryptedMap, encryptedBody);
    }

    public Message decryptMessage(Message message) {
        String decryptedBody = decrypt(message.body());

        Map<String, String> decryptedHeaders = new HashMap<>();
        for (Map.Entry<String,String> entry : message.headers().entrySet()) {
            String decryptedKey = decrypt(entry.getKey());
            String decryptedValue = decrypt(entry.getValue());
            decryptedHeaders.put(decryptedKey, decryptedValue);
        }
        return new Message(decryptedHeaders, decryptedBody);
    }

    public String encrypt(String data) {
        StringBuilder res = new StringBuilder();
        int keyIndex = 0;
        for (int i = 0; i<data.length(); i++) {
            char el = data.charAt(i);
            char encodedEl = el;
            if (symbols.indexOf(el) >= 0){
                int keyElementPosition = symbols.indexOf(key.charAt(keyIndex));
                int encodedElementPosition = symbols.indexOf(el) + keyElementPosition;
                if (encodedElementPosition >= symbols.length()) {
                    encodedElementPosition = encodedElementPosition - symbols.length();
                }
                encodedEl = symbols.charAt(encodedElementPosition);
            }
            res.append(encodedEl);
            keyIndex++;
            if (keyIndex > key.length() - 1) {
                keyIndex = 0;
            }
        }
        return res.toString();
    }

    public String decrypt(String data) {
        StringBuilder res = new StringBuilder();
        int keyIndex = 0;
        for (int i = 0; i<data.length(); i++) {
            char el = data.charAt(i);
            char encodedEl = el;
            if (symbols.indexOf(el) >= 0){
                int keyElementPosition = symbols.indexOf(key.charAt(keyIndex));
                int encodedElementPosition = symbols.indexOf(el) - keyElementPosition;
                if (encodedElementPosition < 0) {
                    encodedElementPosition = symbols.length() + encodedElementPosition;
                }
                encodedEl = symbols.charAt(encodedElementPosition);
            }
            res.append(encodedEl);
            keyIndex++;
            if (keyIndex > key.length() - 1) {
                keyIndex = 0;
            }
        }
        return res.toString();
    }
}
