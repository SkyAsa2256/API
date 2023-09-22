package com.envyful.api.database.sql.type;

import com.envyful.api.database.sql.SingleValuedSqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class TimestampSqlType extends SingleValuedSqlType<Instant> {

    public TimestampSqlType(Instant value) {
        super(value);
    }

    @Override
    public void add(int parameter, PreparedStatement statement) throws SQLException {
        statement.setTimestamp(parameter, new Timestamp(this.value.toEpochMilli()));
    }
}
