package com.envyful.api.config.type;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@ConfigSerializable
public class DateFormatConfig {

    private String format = "dd/MM/yyyy HH:mm:ss";
    private transient DateFormat dateFormat;

    public DateFormatConfig() {}

    public DateFormatConfig(String format) {
        this.format = format;
    }

    public DateFormat getFormat() {
        if (this.dateFormat == null) {
            this.dateFormat = new SimpleDateFormat(this.format);
        }

        return this.dateFormat;
    }

    public String format(Instant instant) {
        return this.format(new Date(instant.toEpochMilli()));
    }

    public String format(Date date) {
        return this.getFormat().format(date);
    }

    public String format(long time) {
        return this.format(new Date(time));
    }
}
