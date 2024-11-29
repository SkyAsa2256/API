package com.envyful.api.sqlite;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;
import com.envyful.api.database.sql.NonCloseableConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * H2 implementation of the {@link Database} interface
 *
 */
public class H2Database implements Database {

    private final NonCloseableConnection connection;

    public H2Database(String filePath) throws SQLException {
        this.loadDriver();
        this.connection = new NonCloseableConnection(DriverManager.getConnection("jdbc:h2:" + filePath));
    }

    private void loadDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to load H2 driver"));
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public void close() {
        try {
            this.connection.forceClose();
        } catch (SQLException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to close H2 connection", e));
        }
    }
}
