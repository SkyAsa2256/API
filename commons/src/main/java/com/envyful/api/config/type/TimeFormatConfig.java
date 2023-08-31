package com.envyful.api.config.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

/**
 *
 * Represents a configurable time format
 * <br>
 * The placeholders work by first going through and replacing any matching
 * values in the format, before then replacing each of the %days% (etc) with the
 * number of days, minutes hours etc.
 * <br>
 * If there is no value for a specific increment of time, and a placeholder contains that
 * time then it will replace it with an empty String. For example,
 * if the %seconds% placeholder contains %seconds% and there is 0 seconds then
 * it will replace the %seconds% in the format with a ""
 * <br>
 * Notably do not use "%seconds_value%" (etc) as placeholder entries otherwise
 * the program (server) will crash!
 * <br>
 * For the standard implementation of the handling of this config section look at the
 * function {@link com.envyful.api.time.UtilTimeFormat#format(long, TimeFormatConfig)}
 *
 */
@ConfigSerializable
public class TimeFormatConfig {

    private String format = "%days%%hours%%minutes%%seconds%";
    private Map<String, String> placeholders = ImmutableMap.of(
            "days", "%days_value% days ",
            "hours", "%hours_value% hours ",
            "minutes", "%minutes_value% minutes ",
            "seconds", "%seconds_value% seconds"
    );

    public TimeFormatConfig() {
    }

    private TimeFormatConfig(Builder builder) {
        this.format = builder.format;
        this.placeholders = builder.placeholders;
    }

    public String getFormat() {
        return this.format;
    }

    public Map<String, String> getPlaceholders() {
        return this.placeholders;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String format;
        private Map<String, String> placeholders = Maps.newHashMap();

        private Builder() {}

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder placeholder(String placeholder, String replacement) {
            this.placeholders.put(placeholder, replacement);
            return this;
        }

        public TimeFormatConfig build() {
            return new TimeFormatConfig(this);
        }
    }
}
