package com.envyful.api.type;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Static utility class for parsing types
 *
 */
public class UtilParse {

    private static final Pattern INT_PATTERN = Pattern.compile("^[-+]?\\d+$");
    private static final Pattern LONG_PATTERN = Pattern.compile("^[-+]?\\d+$\n");
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("^[-+]?\\d+(\\.\\d+)?$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    /**
     *
     * Parses the arg to an integer
     *
     * @param arg The arg to parse
     * @return The potential parsed integer
     * @deprecated Use {@link UtilParse#parseInt(String)}
     */
    @Deprecated
    public static Optional<Integer> parseInteger(String arg) {
        if (arg == null) {
            return Optional.empty();
        }

        Matcher matcher = INT_PATTERN.matcher(arg);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        return Optional.of(Integer.parseInt(arg));
    }

    /**
     *
     * Parses the arg to an integer
     *
     * @param arg The arg to parse
     * @return The potential parsed integer
     */
    public static OptionalInt parseInt(String arg) {
        if (arg == null) {
            return OptionalInt.empty();
        }

        Matcher matcher = INT_PATTERN.matcher(arg);

        if (!matcher.matches()) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(Integer.parseInt(arg));
    }

    /**
     *
     * Parses the arg to a long
     *
     * @param arg The arg to parse
     * @return The potential parsed long
     */
    public static OptionalLong parseLong(String arg) {
        if (arg == null) {
            return OptionalLong.empty();
        }

        Matcher matcher = LONG_PATTERN.matcher(arg);

        if (!matcher.matches()) {
            return OptionalLong.empty();
        }

        return OptionalLong.of(Long.parseLong(arg));
    }

    /**
     *
     * Parses the arg to a double
     *
     * @param arg The arg to parse
     * @return The potential parsed double
     */
    public static OptionalDouble parseDouble(String arg) {
        if (arg == null) {
            return OptionalDouble.empty();
        }

        Matcher matcher = DOUBLE_PATTERN.matcher(arg);

        if (!matcher.matches()) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(Double.parseDouble(arg));
    }

    /**
     *
     * Parses the arg to a {@link UUID}
     *
     * @param arg The arg to parse
     * @return The potential uuid
     */
    public static Optional<UUID> parseUuid(String arg) {
        if (arg == null) {
            return Optional.empty();
        }

        Matcher matcher = UUID_PATTERN.matcher(arg);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        return Optional.of(UUID.fromString(arg));
    }
}
