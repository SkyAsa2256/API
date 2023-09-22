package com.envyful.api.database.sql;

/**
 *
 * Represents an SQL type that only has one value
 *
 * @param <A> The value type
 */
public abstract class SingleValuedSqlType<A> implements SqlType {

    protected final A value;

    /**
     *
     * A constructor taking the value to be stored
     *
     * @param value The value
     */
    protected SingleValuedSqlType(A value) {
        this.value = value;
    }
}
