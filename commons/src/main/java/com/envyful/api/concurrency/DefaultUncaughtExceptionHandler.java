package com.envyful.api.concurrency;

import org.apache.logging.log4j.Logger;

public class DefaultUncaughtExceptionHandler
        implements Thread.UncaughtExceptionHandler {
    private final Logger logger;

    public DefaultUncaughtExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void uncaughtException(
            Thread thread,
            Throwable exception) {
        if (this.logger == null) {
            return;
        }

        this.logger.error(
                "Caught previously unhandled exception :",
                exception
        );
    }
}
