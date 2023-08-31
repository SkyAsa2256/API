package com.envyful.api.time;

import com.envyful.api.config.type.TimeFormatConfig;
import org.junit.jupiter.api.Test;

class UtilTimeFormatTests {

    @Test
    void testTimeFormatConfig() {
        assert UtilTimeFormat.format(10_000_000L, new TimeFormatConfig()).equals("2 hours 46 minutes 40 seconds");
    }

    @Test
    void testSmallerTimeFormatConfig() {
        assert UtilTimeFormat.format(10_000L, new TimeFormatConfig()).equals("10 seconds");
    }

    @Test
    void testFailureForInvalidPlaceholdersSeconds() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("seconds_value", "seconds_value")
                .build()).isEmpty();
    }

    @Test
    void testFailureForInvalidPlaceholdersMinutes() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("minutes_value", "seconds_value")
                .build()).isEmpty();
    }

    @Test
    void testFailureForInvalidPlaceholdersHours() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("hours_value", "seconds_value")
                .build()).isEmpty();
    }

    @Test
    void testFailureForInvalidPlaceholdersDays() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("days_value", "seconds_value")
                .build()).isEmpty();
    }

    @Test
    void testTimeFormatConfigTrimEnd() {
        assert UtilTimeFormat.format(10_000_000L, TimeFormatConfig.builder()
                .format("%days%%hours%%minutes%%seconds%")
                .placeholder("days", "%days_value% days ")
                .placeholder("hours", "%hours_value% hours ")
                .placeholder("minutes", "%minutes_value% minutes ")
                .placeholder("seconds", "%seconds_value% seconds  ")
                .build()).equals("2 hours 46 minutes 40 seconds");
    }
}
