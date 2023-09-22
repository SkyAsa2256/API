package com.envyful.api.database.sql;

import com.envyful.api.database.sql.type.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

/**
 * 
 * A class representing data of a specific type
 * <br>
 * Designed to avoid usage of {@link PreparedStatement#setObject(int, Object)}
 * 
 */
public interface SqlType {

    /**
     * 
     * Adds the type to the prepared statement
     * 
     * @param parameter The parameter index (starting from 1)
     * @param statement The statement to set
     * @throws SQLException any errors that occur
     */
    void add(int parameter, PreparedStatement statement) throws SQLException;

    /**
     *
     * Gets an integer type
     *
     * @param i The integer
     * @return The type instance
     */
    static SqlType integer(int i) {
        return new IntegerSqlType(i);
    }

    /**
     *
     * Gets a double type
     *
     * @param i The double
     * @return The type instance
     */
    static SqlType decimal(double i) {
        return new DoubleSqlType(i);
    }

    /**
     *
     * Gets a float type
     *
     * @param i The float
     * @return The type instance
     */
    static SqlType floatingDecimal(float i) {
        return new FloatSqlType(i);
    }

    /**
     *
     * Gets a boolean type
     *
     * @param value The boolean
     * @return The type instance
     */
    static SqlType bool(boolean value) {
        return new BooleanSqlType(value);
    }

    /**
     *
     * Gets a long type
     *
     * @param value The long
     * @return The type instance
     */
    static SqlType bigInt(long value) {
        return new LongSqlType(value);
    }

    /**
     *
     * Gets an instant type
     *
     * @param instant The instant
     * @return The type instance
     */
    static SqlType timestamp(Instant instant) {
        return new TimestampSqlType(instant);
    }

    /**
     *
     * Gets a String type
     *
     * @param value The string
     * @return The type instance
     */
    static SqlType text(String value) {
        return new StringSqlType(value);
    }
}
