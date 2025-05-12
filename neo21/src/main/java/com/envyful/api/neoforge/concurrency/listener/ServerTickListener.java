package com.envyful.api.neoforge.concurrency.listener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Simple listener class for running tasks on the minecraft thread.
 *
 */
public class ServerTickListener {

    private final Set<Runnable> tasks = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Pre event) {
        Iterator<Runnable> iterator = tasks.iterator();

        while (iterator.hasNext()) {
            Runnable next = iterator.next();
            next.run();
            iterator.remove();
        }
    }

    public void addTask(Runnable runnable) {
        this.tasks.add(runnable);
    }

    public boolean hasTask(Runnable runnable) {
        return tasks.contains(runnable);
    }
}
