package com.envyful.api.database.sql.type;

import com.envyful.api.database.sql.SingleValuedSqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoubleSqlType extends SingleValuedSqlType<Double> {

    public DoubleSqlType(Double value) {
        super(value);
    }

    @Override
    public void add(int parameter, PreparedStatement statement) throws SQLException {
        statement.setDouble(parameter, this.value);
    }
}
