package com.envyful.api.concurrency;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 *
 * Static utility class used by mods, and plugins, to prove a logging
 * interface for the generic API classes found in this repository.
 * <br>
 * Best practice is to use the {@link UtilLogger#getLogger()} method
 * as it is not guaranteed that every mod will provide a logger to the API
 * and so it will default to the "EnvyAPI" logger when no logger is provided
 *
 */
public class UtilLogger {

    private static final Logger LOGGER = LogManager.getLogger("EnvyAPI");

    private static Logger logger;

    private UtilLogger() {}

    /**
     *
     * Sets the logger for the API to use
     *
     * @param logger The logger
     */
    public static void setLogger(Logger logger) {
        UtilLogger.logger = logger;
    }

    /**
     *
     * Safely attempts to get the logger provided by the mod/plugin
     *
     * @return The logger, if it exists
     * @deprecated Use {@link UtilLogger#getLogger()} instead
     */
    @Deprecated(since = "7.5.9", forRemoval = true)
    public static Optional<Logger> logger() {
        if (logger == null) {
            return Optional.of(LOGGER);
        }

        return Optional.ofNullable(logger);
    }

    /**
     *
     * Safely attempts to get the logger provided by the mod/plugin
     * <br>
     * If the logger has not been set, it will return the API's default logger
     *
     * @return The logger, if it exists
     */
    public static Logger getLogger() {
        if (logger == null) {
            return LOGGER;
        }

        return logger;
    }
}
