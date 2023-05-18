package com.envyful.api.database;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
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
    default Connection getConnection()
            throws SQLException,UnsupportedOperationException {
        throw new UnsupportedOperationException("Not an SQL database");
    }

    /**
     *
     * Gets the Jedis connection from the database if available
     *
     * @return The jedis connection
     * @throws UnsupportedOperationException If this database isn't redis lol
     */
    default StatefulRedisConnection<String, String> getRedis()
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Gets the client
     *
     * @return The client
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default RedisClient getClient() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Gets the URI
     *
     * @return The URI
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default RedisURI getURI() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Subscribes the given object using the annotations
     *
     * @param o The object
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default void subscribe(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Publishes a message to the redis over the specified channel
     *
     * @param channel The channel
     * @param message The message
     * @throws UnsupportedOperationException If this database isn't redis
     */
    default void publish(String channel, String message)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not a redis database");
    }

    /**
     *
     * Closes the connection
     *
     */
    void close();

}
