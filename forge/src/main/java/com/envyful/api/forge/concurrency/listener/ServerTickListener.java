package com.envyful.api.forge.concurrency.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import scala.tools.nsc.Global;

import java.util.List;
import java.util.Set;

/**
 *
 * Simple listener class for running tasks on the minecraft thread.
 *
 */
public class ServerTickListener {

    private final Set<Runnable> tasks = Sets.newHashSet();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (Runnable task : this.tasks) {
            task.run();
        }

        this.tasks.clear();
    }

    public void addTask(Runnable runnable) {
        this.tasks.add(runnable);
    }

    public boolean hasTask(Runnable runnable) {
        return tasks.contains(runnable);
    }
}
