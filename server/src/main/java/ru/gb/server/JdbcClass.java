package ru.gb.server;

import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.sql.*;

public class JdbcClass implements Closeable {
    private final Logger logger;
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatementInsert;
    private PreparedStatement preparedStatementUsernameUpdate;
    private PreparedStatement preparedStatementCheckExist;

    private void initPreparedStatements() throws SQLException {
        preparedStatementInsert = connection.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)");
        preparedStatementUsernameUpdate = connection.prepareStatement("UPDATE user SET username = ? WHERE username = ?");
        preparedStatementCheckExist = connection.prepareStatement("SELECT COUNT(*) FROM user WHERE username = ? AND password = ?");
    }

    public JdbcClass(Logger logger) {
        this.logger = logger;
        try {
            connect();
            initPreparedStatements();
        } catch (Exception e) {
            e.printStackTrace();
            close();
        }
    }

    void clearUsers() throws SQLException {
        statement.executeUpdate("DELETE FROM user");
    }

    void insertUser(String username, String password) throws SQLException {
        preparedStatementInsert.setString(1, username);
        preparedStatementInsert.setString(2, password);
        preparedStatementInsert.execute();
    }

    String updateUsername(String oldUsername, String newUsername) {
        try {
            preparedStatementUsernameUpdate.setString(1, newUsername);
            preparedStatementUsernameUpdate.setString(2, oldUsername);
            preparedStatementUsernameUpdate.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.trace("Nick changed from " + oldUsername + " to " + newUsername);
            return oldUsername;
        }
        return newUsername;
    }

    boolean authUser(String username, String password) throws SQLException {
        preparedStatementCheckExist.setString(1, username);
        preparedStatementCheckExist.setString(2, password);
        ResultSet rs = preparedStatementCheckExist.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        return count == 1;
    }

    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:personal-cloud-storage.db");
        statement = connection.createStatement();
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
