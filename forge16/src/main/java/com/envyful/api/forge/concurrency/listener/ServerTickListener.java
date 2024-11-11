package com.envyful.api.forge.concurrency.listener;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Simple listener class for running tasks on the minecraft thread.
 *
 */
public class ServerTickListener {

    private final Set<Runnable> tasks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

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
