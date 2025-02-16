package com.envyful.api.time;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.type.TimeFormatConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Static utility class for formatting date time instances and the TimeFormatConfig
 *
 */
public class UtilTimeFormat {

    private static final DateFormat DATE_FORMATTER =
            new SimpleDateFormat("dd/MM/yyyy");
    private static final long SECONDS_PER_MINUTE = 60;
    private static final long MINUTES_PER_HOUR = 60;
    private static final long SECONDS_PER_HOUR =
            SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    private static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\%([a-z]+)\\%");

    private static final Map<String, DateFormat> DATE_FORMATS = new HashMap<>();

    /**
     *
     * Formats the duration, converting it into milliseconds from the specified
     * unit, to the specified format in the {@link TimeFormatConfig} provided.
     *
     * @param duration The duration
     * @param config The format config
     * @return The formatted duration
     * @deprecated Use {@link TimeFormatConfig#format(Duration)}
     */
    public static String format(Duration duration, TimeFormatConfig config) {
        return format(duration.toMillis(), config);
    }

    /**
     *
     * Formats the duration, converting it into milliseconds from the specified
     * unit, to the specified format in the {@link TimeFormatConfig} provided.
     *
     * @param time The time duration
     * @param timeUnit The time unit
     * @param config The format config
     * @return The formatted duration
     * @deprecated Use {@link TimeFormatConfig#format(long, TimeUnit)}
     */
    public static String format(long time, TimeUnit timeUnit, TimeFormatConfig config) {
        return format(timeUnit.toMillis(time), config);
    }

    /**
     *
     * Formats the duration between {@link System#currentTimeMillis()} and the provided
     * time using the {@link TimeFormatConfig} provided.
     *
     * @param time A timestamp in the future
     * @param config The config
     * @return The formatted time
     */
    public static String getTimeUntil(long time, TimeFormatConfig config) {
        return format(time - System.currentTimeMillis(), config);
    }

    /**
     *
     * Formats the duration, converting it into milliseconds from the specified
     * unit, to the specified format in the {@link TimeFormatConfig} provided.
     *
     * @param duration The duration
     * @param unit The time unit
     * @param config The format config
     * @return The formatted duration
     * @deprecated Use {@link TimeFormatConfig#format(long, TimeUnit)}
     */
    @Deprecated
    public static String formatWithUnit(long duration, TimeUnit unit, TimeFormatConfig config) {
        return format(unit.toMillis(duration), config);
    }

    /**
     *
     * Formats the duration, in milliseconds, to the specified format in the
     * {@link TimeFormatConfig} provided.
     * <br>
     * For details on how that config section works pleased read the JavaDocs
     * found in the {@link TimeFormatConfig} class.
     * <br>
     * The time provided <b>MUST</b> be in milliseconds
     *
     * @param time The time duration in milliseconds
     * @param config The formatting config
     * @return The formatted duration
     * @deprecated Use {@link TimeFormatConfig#format(long)}
     */
    @Deprecated
    public static String format(long time, TimeFormatConfig config) {
        if (containsInvalidPlaceholders(config.getPlaceholders().keySet())) {
            UtilLogger.getLogger().error("Invalid placeholders found in TimeFormatConfig - please avoid using %seconds_value%, %minutes_value%, %hours_value%, %days_value% as placeholder keys");
            return "";
        }

        long seconds = TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS);

        long daysPart = (seconds / SECONDS_PER_DAY);
        long hoursPart = (seconds / SECONDS_PER_HOUR) % 24;
        long minutesPart = (seconds / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
        long secondsPart = (seconds) % SECONDS_PER_MINUTE;

        String format = config.getFormat();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(format);

        while (matcher.find()) {
            String replacement = config.getPlaceholders().getOrDefault(matcher.group(1), "");

            if (shouldClearReplacement(replacement, "seconds", secondsPart) ||
                    shouldClearReplacement(replacement, "minutes", minutesPart) ||
                    shouldClearReplacement(replacement, "hours", hoursPart) ||
                    shouldClearReplacement(replacement, "days", daysPart)) {
                replacement = "";
            }

            format = matcher.replaceFirst(replacement);
            matcher = PLACEHOLDER_PATTERN.matcher(format);
        }

        return format
                .replace("%seconds_value%", String.valueOf(secondsPart))
                .replace("%minutes_value%", String.valueOf(minutesPart))
                .replace("%hours_value%", String.valueOf(hoursPart))
                .replace("%days_value%", String.valueOf(daysPart))
                .trim();
    }

    private static boolean containsInvalidPlaceholders(Set<String> keySet) {
        return keySet.contains("seconds_value") ||
                keySet.contains("minutes_value") ||
                keySet.contains("hours_value") ||
                keySet.contains("days_value");
    }

    private static boolean shouldClearReplacement(String originalText, String placeholder, long duration) {
        return originalText.contains("%" + placeholder + "_value%") && duration <= 0;
    }

    public static String format(Date date, String format) {
        return DATE_FORMATS.computeIfAbsent(format,
                unused -> new SimpleDateFormat(format)).format(date);
    }

    public static String format(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static String getTimeUntil(long timeMillis) {
        long timeUntil = timeMillis - System.currentTimeMillis();
        Duration duration = Duration.ofMillis(timeUntil);

        var days = duration.get(ChronoUnit.SECONDS) / (60 * 60 * 24);

        if (days > 0) {
            return String.format("%d day(s) %02d hour(s) %02d minute(s) and %02d second(s)",
                    days,
                    (duration.get(ChronoUnit.SECONDS) / (60 * 60)) % 24,
                    (duration.get(ChronoUnit.SECONDS) / 60) % 60,
                    duration.get(ChronoUnit.SECONDS) % 60
            );
        }

        return String.format("%d hour(s) %02d minute(s) and %02d second(s)",
                duration.get(ChronoUnit.SECONDS) / (60 * 60),
                (duration.get(ChronoUnit.SECONDS) / 60) % 60,
                duration.get(ChronoUnit.SECONDS) % 60
        );
    }

    public static String getFormattedDuration(long playTime) {
        long seconds = TimeUnit.SECONDS.convert(playTime, TimeUnit.MILLISECONDS);

        long daysPart = (seconds / SECONDS_PER_DAY);
        long hoursPart = (seconds / SECONDS_PER_HOUR) % 24;
        long minutesPart = (seconds / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
        long secondsPart = (seconds) % SECONDS_PER_MINUTE;

        StringBuilder builder = new StringBuilder();

        if (daysPart > 0) {
            builder.append(daysPart).append("d ");
        }

        if (hoursPart > 0) {
            builder.append(hoursPart).append("h ");
        }

        if (minutesPart > 0) {
            builder.append(minutesPart).append("m ");
        }

        if (secondsPart > 0) {
            builder.append(secondsPart).append("s");
        }

        return builder.toString();
    }
}
