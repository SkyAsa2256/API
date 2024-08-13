package com.envyful.api.concurrency;

/**
 *
 * Default implementation of the {@link Thread.UncaughtExceptionHandler} that logs the exception
 * to the {@link UtilLogger} if it is present
 *
 */
public class DefaultUncaughtExceptionHandler
        implements Thread.UncaughtExceptionHandler {

    public DefaultUncaughtExceptionHandler() {}

    @Override
    public void uncaughtException(
            Thread thread,
            Throwable exception) {
        UtilLogger.logger().ifPresent(logger -> logger.error(
                "Caught previously unhandled exception :",
                exception
        ));
    }
}
