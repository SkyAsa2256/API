package com.envyful.api.time;

import com.envyful.api.config.type.TimeFormatConfig;
import org.junit.jupiter.api.Test;

class UtilTimeFormatTests {

    // Checking the function provides correct output
    @Test
    void testTimeFormatConfig() {
        assert UtilTimeFormat.format(10_000_000L, new TimeFormatConfig()).equals("2 hours 46 minutes 40 seconds");
    }

    // Checking the function provides correct output and removes non-seconds values
    @Test
    void testSmallerTimeFormatConfig() {
        assert UtilTimeFormat.format(10_000L, new TimeFormatConfig()).equals("10 seconds");
    }

    // Checking the function cannot enter an infinite loop from the user entering seconds_value as a placholder
    @Test
    void testFailureForInvalidPlaceholdersSeconds() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("seconds_value", "seconds_value")
                .build()).isEmpty();
    }

    // Checking the function cannot enter an infinite loop from the user entering minutes_value as a placholder
    @Test
    void testFailureForInvalidPlaceholdersMinutes() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("minutes_value", "seconds_value")
                .build()).isEmpty();
    }

    // Checking the function cannot enter an infinite loop from the user entering hours_value as a placholder
    @Test
    void testFailureForInvalidPlaceholdersHours() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("hours_value", "seconds_value")
                .build()).isEmpty();
    }

    // Checking the function cannot enter an infinite loop from the user entering days_value as a placholder
    @Test
    void testFailureForInvalidPlaceholdersDays() {
        assert UtilTimeFormat.format(10_000L, TimeFormatConfig.builder()
                        .format("%days%%hours%%minutes%%seconds%")
                        .placeholder("days_value", "seconds_value")
                .build()).isEmpty();
    }

    // Checking that the blank string at the end of the seconds placeholder gets trimmed (no blank text at the end of the string)
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
