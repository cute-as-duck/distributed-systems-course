package server.config;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig {
    public static final String URL_PROPERTY = "connection.url";
    public static final String USERNAME_PROPERTY = "connection.username";
    public static final String PASSWORD_PROPERTY = "connection.password";
    public static final String DRIVER_PROPERTY = "connection.driver_class";
    public static final String LIQUIBASE_CHANGELOG_FILE = "db/changelog/liquibase-changelog.xml";

    public static Connection getConnection() throws SQLException {
        final String url = AppProps.getProp(URL_PROPERTY);
        final String username = AppProps.getProp(USERNAME_PROPERTY);
        final String password = AppProps.getProp(PASSWORD_PROPERTY);
        try {
            Class.forName(AppProps.getProp(DRIVER_PROPERTY));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, username, password);
    }

    public static void updateDatabaseStructure(final Connection connection) throws Exception {
        final Map<String, Object> config = new HashMap<>();
        Scope.child(config, () -> {
            final DatabaseConnection dbConnection = new JdbcConnection(connection);
            final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(dbConnection);
            CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database);
            updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, LIQUIBASE_CHANGELOG_FILE);
            updateCommand.execute();
        });
    }
}
