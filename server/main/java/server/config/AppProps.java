package server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProps {

    private static Properties props;

    static {
        props = new Properties();
        try (InputStream is = new FileInputStream("main/resources/config.properties")) {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProp (String propKey) {
        return props.getProperty(propKey);
    }
}
