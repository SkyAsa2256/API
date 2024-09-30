package com.envyful.api.sqlite;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase implements Database {

    private final Connection connection;

    public SQLiteDatabase(String file) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file);
    }

    @Override
    public Connection getConnection() throws SQLException, UnsupportedOperationException {
        if (this.connection == null) {
            throw new SQLException("Failed to connect to database");
        }

        return this.connection;
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to close connection", e));
        }
    }
}
