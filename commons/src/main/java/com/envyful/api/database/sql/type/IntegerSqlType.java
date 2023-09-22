package com.envyful.api.database.sql.type;

import com.envyful.api.database.sql.SingleValuedSqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntegerSqlType extends SingleValuedSqlType<Integer> {

    public IntegerSqlType(Integer value) {
        super(value);
    }

    @Override
    public void add(int parameter, PreparedStatement statement) throws SQLException {
        statement.setInt(parameter, this.value);
    }
}
