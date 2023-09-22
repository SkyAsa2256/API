package com.envyful.api.database.sql.type;

import com.envyful.api.database.sql.SingleValuedSqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooleanSqlType extends SingleValuedSqlType<Boolean> {

    public BooleanSqlType(Boolean value) {
        super(value);
    }

    @Override
    public void add(int parameter, PreparedStatement statement) throws SQLException {
        statement.setBoolean(parameter, this.value);
    }
}
