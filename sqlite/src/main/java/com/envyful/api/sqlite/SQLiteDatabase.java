package com.envyful.api.sqlite;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;
import com.envyful.api.database.sql.util.NonCloseableConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * SQLite implementation of the {@link Database} interface
 *
 */
public class SQLiteDatabase implements Database {

    private final NonCloseableConnection connection;

    public SQLiteDatabase(String filePath) throws SQLException {
        this.loadDriver();
        this.connection = new NonCloseableConnection(DriverManager.getConnection("jdbc:sqlite:" + filePath));
    }

    private void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
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
