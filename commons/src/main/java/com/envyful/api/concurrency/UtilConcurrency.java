package com.envyful.api.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Static utility class for running tasks off the main thread using an {@link ExecutorService}.
 * For a potentially more efficient, platform specific implementation check the platform specific module.
 * Should be named using the following format Util<Platform>Concurrency. For example:
 *          - UtilForgeConcurrency
 *          - UtilSpigotConcurrency
 *
 */
public class UtilConcurrency {

    /**
     *
     * A cached thread pool executor service instance for running tasks off the main server thread
     *
     */
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     *
     * Takes the runnable and passes it to the {@link UtilConcurrency#EXECUTOR_SERVICE} to be executed using one of the
     * cached threads. (typically minimal [or no set] delay)
     *
     * @param runnable The runnable to execute asynchronously
     */
    public static void runAsync(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }

}
