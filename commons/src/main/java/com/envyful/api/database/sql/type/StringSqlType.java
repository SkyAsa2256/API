package com.envyful.api.database.sql.type;

import com.envyful.api.database.sql.SingleValuedSqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringSqlType extends SingleValuedSqlType<String> {

    public StringSqlType(String value) {
        super(value);
    }

    @Override
    public void add(int parameter, PreparedStatement statement) throws SQLException {
        statement.setString(parameter, this.value);
    }
}
