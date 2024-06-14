package server;

import server.config.DatabaseConfig;
import server.web.BroadcastController;
import server.web.ChatServer;

import java.sql.Connection;

public class App {

    private void run() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            DatabaseConfig.updateDatabaseStructure(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatServer chatServer = new ChatServer();
        BroadcastController broadcast = new BroadcastController();
        broadcast.chatServer(chatServer);
        chatServer.start();
    }

    public static void main(String[] args) {
        new App().run();
    }
}
