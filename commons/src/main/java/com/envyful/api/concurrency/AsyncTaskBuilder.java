package com.envyful.api.concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * Builder class for repeating tasks on Forge
 *
 */
public class AsyncTaskBuilder {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(5);

    private long delayMillis = 0;
    private long intervalMillis = 10L;

    private Runnable task;

    public AsyncTaskBuilder() {}

    /**
     *
     * The delay in millis before the task should begin
     *
     * @param delayMillis time in millis before the task starts
     * @return The builder
     */
    public AsyncTaskBuilder delay(long delayMillis) {
        this.delayMillis = delayMillis;
        return this;
    }

    /**
     *
     * The interval between each execution
     *
     * @param intervalMillis The milliseconds between each execution
     * @return The builder
     */
    public AsyncTaskBuilder interval(long intervalMillis) {
        this.intervalMillis = intervalMillis;
        return this;
    }


    /**
     *
     * Sets the task to be executed
     *
     * @param task The task
     * @return The builder
     */
    public AsyncTaskBuilder task(Runnable task) {
        this.task = task;
        return this;
    }

    /**
     *
     * Runs the task
     *
     */
    public void start() {
        if (this.task == null) {
            throw new RuntimeException("Task cannot be null");
        }

        EXECUTOR_SERVICE.scheduleAtFixedRate(this.task, this.delayMillis, this.intervalMillis, TimeUnit.MILLISECONDS);
    }
}
