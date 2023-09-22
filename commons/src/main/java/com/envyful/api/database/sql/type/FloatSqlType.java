package com.envyful.api.database.sql.type;

import com.envyful.api.database.sql.SingleValuedSqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FloatSqlType extends SingleValuedSqlType<Float> {

    public FloatSqlType(Float value) {
        super(value);
    }

    @Override
    public void add(int parameter, PreparedStatement statement) throws SQLException {
        statement.setFloat(parameter, this.value);
    }
}
