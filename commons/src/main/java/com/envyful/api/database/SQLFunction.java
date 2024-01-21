package com.envyful.api.database;

import java.sql.SQLException;

/**
 *
 * Represents a function that takes in a value and returns a value
 * and can throw an SQLException
 *
 * @param <A> The input type
 * @param <B> The output type
 */
@FunctionalInterface
public interface SQLFunction<A, B> {

    /**
     *
     * Applies the function
     *
     * @param a The input
     * @return The output
     * @throws SQLException An error if there's an issue
     */
    B apply(A a) throws SQLException;

}
