package com.envyful.api.time;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTime {

    private static final Pattern timeString = Pattern.compile("(((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?)");

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

    public static long getStartOfDay() {
        return getStartOfDay(TimeUnit.MILLISECONDS);
    }

    public static long getStartOfDay(TimeUnit timeUnit) {
        var currentDate = LocalDate.now();
        var startOfDay = currentDate.atStartOfDay();
        var zonedStartOfDay = startOfDay.atZone(ZoneId.systemDefault());
        var timestampStartOfDay = timeUnit.convert(zonedStartOfDay.toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);

        return timestampStartOfDay;
    }
}
