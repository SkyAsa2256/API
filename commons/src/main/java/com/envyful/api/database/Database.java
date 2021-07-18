package com.envyful.api.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * Interface representing a database connection
 *
 */
public interface Database {

    /**
     *
     * Gets the SQL connection
     *
     * @return An SQL Connection
     * @throws SQLException An error if there's no connections
     * @throws UnsupportedOperationException If this isn't an SQL database
     */
    Connection getConnection() throws SQLException,UnsupportedOperationException;

    /**
     *
     * Closes the connection
     *
     */
    void close();

}
