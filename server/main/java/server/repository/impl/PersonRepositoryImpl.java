package server.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.config.DatabaseConfig;
import server.dto.AuthRequest;
import server.dto.Person;
import server.model.PersonDetails;
import server.repository.PersonRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonRepositoryImpl implements PersonRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PersonRepositoryImpl.class);
    private static final String C_ID = "id";
    private static final String C_USERNAME = "username";
    private static final String C_PASSWORD = "password";
    private static final String C_STATUS = "status";
    private static final String C_HOST = "host";
    private static final String C_PORT = "port";

    @Override
    public PersonDetails addPerson(PersonDetails personDetails) {
        String sql = "INSERT INTO persons (username, password, host, port) VALUES(?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            connection.setAutoCommit(false);
            statement.setString(1, personDetails.username());
            statement.setString(2, personDetails.password());
            statement.setString(3, personDetails.host());
            statement.setInt(4, personDetails.port());

            statement.executeUpdate();
            connection.commit();
            return personDetails;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Person> getOnlinePersons() {
        String sql = "SELECT username, host, port FROM persons WHERE status LIKE 'online'";
        List<Person> list = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement())
        {
            connection.setAutoCommit(false);
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString(C_USERNAME);
                String host = rs.getString(C_HOST);
                int port = rs.getInt(C_PORT);
                Person person = new Person(name, host, port);
                list.add(person);
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<PersonDetails> findByName(String username) {
        String sql = "SELECT * FROM persons WHERE username LIKE ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            connection.setAutoCommit(false);
            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();

            connection.commit();

            if (!rs.next()) {
                return Optional.empty();
            } else {
                System.out.println(rs.getString("password"));
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PersonDetails updatePerson(PersonDetails personDetails) {
        String sql = "UPDATE persons SET status = ?, host = ?, port = ? WHERE username LIKE ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            connection.setAutoCommit(false);
            statement.setString(1, personDetails.status());
            statement.setString(2, personDetails.host());
            statement.setInt(3, personDetails.port());
            statement.setString(4, personDetails.username());

            statement.executeUpdate();
            connection.commit();
            return personDetails;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setOfflineStatus(String username) {
        String sql = "UPDATE persons SET status = 'offline' WHERE username LIKE ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            connection.setAutoCommit(false);
            statement.setString(1, username);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PersonDetails mapRow(ResultSet rs) throws SQLException{
        return new PersonDetails.Builder()
                .id(rs.getLong(C_ID))
                .username(rs.getString(C_USERNAME))
                .password(rs.getString(C_PASSWORD))
                .status(rs.getString(C_STATUS))
                .host(rs.getString(C_HOST))
                .port(rs.getInt(C_PORT))
                .build();
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }
}
