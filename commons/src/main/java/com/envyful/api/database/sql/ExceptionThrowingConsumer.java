package com.envyful.api.database.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface ExceptionThrowingConsumer<T> {

    void accept(T t) throws SQLException;

}
