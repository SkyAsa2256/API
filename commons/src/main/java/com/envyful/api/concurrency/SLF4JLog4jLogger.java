package com.envyful.api.concurrency;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

public class SLF4JLog4jLogger extends AbstractLogger {

    private static final org.slf4j.Marker MARKER = MarkerFactory.getMarker("SLF4JLog4jLogger");
    private final Logger slf4jLogger;

    protected SLF4JLog4jLogger(String name, MessageFactory messageFactory, Logger logger) {
        super(name, messageFactory);

        this.slf4jLogger = logger;
    }

    @Override
    public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable throwable) {
        switch (level.getStandardLevel()) {
            case OFF:
                break;
            case TRACE:
                slf4jLogger.trace(marker(marker), message.getFormattedMessage(), throwable);
                break;
            case DEBUG:
                slf4jLogger.debug(marker(marker), message.getFormattedMessage(), throwable);
                break;
            case INFO:
                slf4jLogger.info(marker(marker), message.getFormattedMessage(), throwable);
                break;
            case WARN:
                slf4jLogger.warn(marker(marker), message.getFormattedMessage(), throwable);
                break;
            case ERROR:
            case FATAL:
                slf4jLogger.error(marker(marker), message.getFormattedMessage(), throwable);
                break;
            default:
                throw new IllegalStateException("Unknown log level " + level);
        }
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Throwable throwable) {
        switch (level.getStandardLevel()) {
            case TRACE:
                return slf4jLogger.isTraceEnabled(marker(marker));
            case DEBUG:
                return slf4jLogger.isDebugEnabled(marker(marker));
            case INFO:
                return slf4jLogger.isInfoEnabled(marker(marker));
            case WARN:
                return slf4jLogger.isWarnEnabled(marker(marker));
            case ERROR:
            case FATAL:
                return slf4jLogger.isErrorEnabled(marker(marker));
            default:
                throw new IllegalStateException("Unknown log level " + level);
        }
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object... params) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return this.isEnabled(level, marker, message);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Message message, Throwable throwable) {
        return this.isEnabled(level, marker, message, throwable);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t) {
        return this.isEnabled(level, marker, String.join("", message), t);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Object message, Throwable t) {
        return this.isEnabled(level, marker, (String) message, t);
    }

    @Override
    public Level getLevel() {
        return Level.getLevel(slf4jLogger.isTraceEnabled() ? "TRACE" : "DEBUG");
    }

    public static org.slf4j.Marker marker(org.apache.logging.log4j.Marker log4jMarker) {
        if (log4jMarker == null) {
            return null;
        }

        String markerName = log4jMarker.getName();
        return MarkerFactory.getMarker(markerName);
    }
}