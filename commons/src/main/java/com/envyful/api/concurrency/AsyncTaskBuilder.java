package com.envyful.api.concurrency;

import javax.annotation.Nullable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 *
 * Builder class for repeating tasks on Forge
 *
 */
public class AsyncTaskBuilder {

    protected long delayMillis = 0;
    protected long intervalMillis = 10L;
    protected Runnable task;
    protected Runnable cancelTask = null;
    protected Supplier<Boolean> cancelCondition = null;

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
     * Will fire when the task is canceled, but only if {@link AsyncTaskBuilder#cancelCondition(Supplier)} is set
     *
     * @param cancelTask The task to run when the cancel condition is met
     * @return The builder
     */
    public AsyncTaskBuilder cancelTask(Runnable cancelTask) {
        this.cancelTask = cancelTask;
        return this;
    }

    /**
     *
     * If the given supplier ever returns true then the task will stop executing
     * Giving a null supplier means it will never cancel (it is null by default)
     *
     * @param cancelCondition The cancel condition
     * @return The builder
     */
    public AsyncTaskBuilder cancelCondition(@Nullable Supplier<Boolean> cancelCondition) {
        this.cancelCondition = cancelCondition;
        return this;
    }

    /**
     *
     * Runs the task
     *
     */
    public void start() {
        if (this.task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        AtomicReference<ScheduledFuture<?>> runningTask = new AtomicReference<>();
        ScheduledFuture<?> scheduledFuture = UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new CancelableRunnable(
                task, this.cancelTask, runningTask, cancelCondition
        ), this.delayMillis, this.intervalMillis, TimeUnit.MILLISECONDS);
        runningTask.set(scheduledFuture);
    }

    public static class CancelableRunnable implements Runnable {

        private final Runnable task;
        private final Runnable cancelTask;
        private final AtomicReference<ScheduledFuture<?>> runningTask;
        private final Supplier<Boolean> cancelCondition;

        public CancelableRunnable(Runnable task, Runnable cancelTask,
                                  AtomicReference<ScheduledFuture<?>> runningTask, Supplier<Boolean> cancelCondition) {
            this.task = task;
            this.cancelTask = cancelTask;
            this.runningTask = runningTask;
            this.cancelCondition = cancelCondition;
        }

        @Override
        public void run() {
            if (this.cancelCondition != null && this.cancelCondition.get()) {
                ScheduledFuture<?> scheduledFuture = this.runningTask.get();

                if (scheduledFuture != null) {
                    scheduledFuture.cancel(false);

                    if (this.cancelTask != null) {
                        this.cancelTask.run();
                    }
                }

                return;
            }

            this.task.run();
        }
    }
}
