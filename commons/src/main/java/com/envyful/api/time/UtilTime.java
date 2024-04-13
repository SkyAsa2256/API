package com.envyful.api.time;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Static utility class for handling time related operations
 *
 */
public class UtilTime {

    private static final Pattern timeString = Pattern.compile("(((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?)");

    /**
     *
     * Attempts to parse a time duration in milliseconds from the provided string
     *
     * @param entry The string to parse
     * @return The time in milliseconds
     */
    public static Optional<Long> attemptParseTime(String entry) {
        Matcher matcher = timeString.matcher(entry);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        return Optional.of(TimeUnit.DAYS.toMillis(parseMatchedString(matcher.group(3)) * 7) +
                TimeUnit.DAYS.toMillis(parseMatchedString(matcher.group(5))) +
                TimeUnit.HOURS.toMillis(parseMatchedString(matcher.group(7))) +
                TimeUnit.MINUTES.toMillis(parseMatchedString(matcher.group(9))) +
                TimeUnit.SECONDS.toMillis(parseMatchedString(matcher.group(11))));
    }

    private static int parseMatchedString(String s) {
        if (s == null) {
            return 0;
        }

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     *
     * Gets the time in milliseconds at the start of the current day
     *
     * @return The time in milliseconds
     */
    public static long getStartOfDay() {
        return getStartOfDay(TimeUnit.MILLISECONDS);
    }

    /**
     *
     * Gets the time in the specified time unit at the start of the current day
     *
     * @param timeUnit The time unit to return the time in
     * @return The time in the specified time unit
     */
    public static long getStartOfDay(TimeUnit timeUnit) {
        var currentDate = LocalDate.now();
        var startOfDay = currentDate.atStartOfDay();
        var zonedStartOfDay = startOfDay.atZone(ZoneId.systemDefault());
        var timestampStartOfDay = timeUnit.convert(zonedStartOfDay.toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);

        return timestampStartOfDay;
    }
}
