package com.envyful.api.forge.concurrency;

import com.envyful.api.forge.concurrency.listener.ServerTickListener;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Forge static utility class for handling concurrency methods specific to forge.
 *
 */
public class UtilForgeConcurrency {

    static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    static final ServerTickListener TICK_LISTENER = new ServerTickListener();

    static {
        MinecraftForge.EVENT_BUS.register(TICK_LISTENER);
    }

    /**
     *
     * Passes runnable task to be run on the main minecraft thread
     *
     * @param runnable The runnable to be run on the main thread
     */
    public static void runSync(Runnable runnable) {
        TICK_LISTENER.addTask(runnable);
    }
}
