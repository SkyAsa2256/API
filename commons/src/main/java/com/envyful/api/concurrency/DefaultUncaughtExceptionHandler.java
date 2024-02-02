package com.envyful.api.concurrency;

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
