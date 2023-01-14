package com.envyful.api.database;

import io.lettuce.core.api.StatefulRedisConnection;

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
     * Gets the Jedis connection from the database if available
     *
     * @return The jedis connection
     * @throws UnsupportedOperationException If this database isn't redis lol
     */
    StatefulRedisConnection<String, String> getRedis() throws UnsupportedOperationException;

    /**
     *
     *
     *
     * @param o
     * @throws UnsupportedOperationException
     */
    void subscribe(Object o) throws UnsupportedOperationException;

    /**
     *
     * Closes the connection
     *
     */
    void close();

}
