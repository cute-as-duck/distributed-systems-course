package chat.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProps {

    private static final String PROP_FILE_NAME = "config.properties";

    private static final String SERVER_HOST_PROP = "server.host";
    private static final String SERVER_PORT_PROP = "server.port";
    private static final String SELF_HOST_PROP = "self.host";
    private static final String SELF_PORT_PROP = "self.port";

    private static Properties props;

    static {
        props = new Properties();
        InputStream inputStream = AppProps.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProp(String key) {
        return props.getProperty(key);
    }

    public static String serverHost() {
        return AppProps.getProp(SERVER_HOST_PROP);
    }

    public static int serverPort() {
        return Integer.parseInt(AppProps.getProp(SERVER_PORT_PROP));
    }

    public static String selfHost() {
        return AppProps.getProp(SELF_HOST_PROP);
    }

    public static int selfPort() {
        return Integer.parseInt(AppProps.getProp(SELF_PORT_PROP));
    }
}
