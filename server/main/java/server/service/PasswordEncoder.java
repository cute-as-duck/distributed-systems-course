package server.service;

import server.config.AppProps;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class PasswordEncoder {

    private static final String SALT_PROP = "password.encoder.salt.value";
    private static final String CHARACTERS_PROP = "password.encoder.characters";
    private static final String ITERATIONS_PROP = "password.encoder.iterations";
    private static final String KEY_LENGTH_PROP = "password.encoder.key.length";


    private static final int iterations = Integer.parseInt(AppProps.getProp(ITERATIONS_PROP));
    private static final int keyLength = Integer.parseInt(AppProps.getProp(KEY_LENGTH_PROP));
    private static final byte[] salt = AppProps.getProp(SALT_PROP).getBytes();

    /* Method to generate the hash value */
    private static byte[] hash(char[] password) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing a password: " + e.getLocalizedMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    /* Method to encrypt the password using the original password and salt value. */
    public static String generateSecurePassword(String password) {

        byte[] securePassword = hash(password.toCharArray());

        return Base64.getEncoder().encodeToString(securePassword);
    }

    /*Method to verify if both password matches or not*/
    public static boolean verifyUserPassword(String providedPassword,
                                             String securedPassword) {
        String newSecuredPassword = generateSecurePassword(providedPassword);
        return newSecuredPassword.equals(securedPassword);
    }
}
