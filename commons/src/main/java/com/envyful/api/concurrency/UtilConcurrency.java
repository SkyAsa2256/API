package com.envyful.api.concurrency;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * Static utility class for running tasks off the main thread using an {@link ExecutorService}.
 * For a potentially more efficient, platform specific
 * implementation check the platform specific module.
 * Should be named using the following
 * format Util(Platform)Concurrency. For example:
 *          - UtilForgeConcurrency
 *          - UtilSpigotConcurrency
 *
 */
public class UtilConcurrency {

    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            Executors.newScheduledThreadPool(10,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("envyware_concurrency_%d")
                    .setUncaughtExceptionHandler(
                            new DefaultUncaughtExceptionHandler()
                    )
                    .build());

    /**
     *
     * Takes the runnable and passes it to the {@link UtilConcurrency#SCHEDULED_EXECUTOR_SERVICE} to be executed using one of the
     * cached threads. (typically minimal [or no set] delay)
     * <br>
     * Returning the completable future that will be completed when the t
     * ask is done
     *
     * @param runnable The runnable to execute asynchronously
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable,
                        SCHEDULED_EXECUTOR_SERVICE)
                .exceptionally(throwable -> {
                    UtilLogger.logger().ifPresent(logger -> logger.error("Error while executing async task", throwable));
                    return null;
                });
    }

    /**
     *
     * Takes the supplier and passes it to the {@link UtilConcurrency#SCHEDULED_EXECUTOR_SERVICE} to be executed using one of the
     * cached threads. (typically minimal [or no set] delay)
     * <br>
     * Returning the completable future that will be completed when
     * the task is done
     *
     * @param supplier The supplier
     * @return The future
     * @param <T> The type to return
     */
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier,
                SCHEDULED_EXECUTOR_SERVICE).exceptionally(throwable -> {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error while executing async task", throwable));
            return null;
        });
    }

    /**
     *
     * Takes the runnable and passes it to the
     * {@link UtilConcurrency#SCHEDULED_EXECUTOR_SERVICE}
     * to be executed later using one of the
     * cached threads
     *
     * @param runnable the runnable to execute asynchronously
     * @param delay The delay before running it
     */
    public static void runLater(Runnable runnable, long delay) {
        SCHEDULED_EXECUTOR_SERVICE.schedule(
                runnable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * Executes the runnable task the tick after the predicate returns true
     *
     * @param predicate The predicate to use
     * @param runnable The runnable to execute
     */
    public static void runLaterWhenTrue(
            Predicate<Runnable> predicate, int delay, Runnable runnable) {
        runLater(() -> attemptRun(predicate, runnable), delay);
    }

    private static void attemptRun(
            Predicate<Runnable> predicate, Runnable runnable) {
        if (!predicate.test(runnable)) {
            runLater(() -> attemptRun(predicate, runnable), 50L);
            return;
        }

        runnable.run();
    }

    public static void runRepeatingTask(
            Runnable runnable, long delay, long period) {
        runRepeatingTask(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    public static void runRepeatingTask(
            Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(
                runnable, delay, period, timeUnit
        );
    }
}
