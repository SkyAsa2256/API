package com.envyful.api.database.sql.type;

import com.envyful.api.database.sql.SingleValuedSqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LongSqlType extends SingleValuedSqlType<Long> {

    public LongSqlType(Long value) {
        super(value);
    }

    @Override
    public void add(int parameter, PreparedStatement statement) throws SQLException {
        statement.setLong(parameter, this.value);
    }
}
