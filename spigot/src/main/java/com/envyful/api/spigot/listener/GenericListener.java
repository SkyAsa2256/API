package com.envyful.api.spigot.listener;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class GenericListener<T extends Event> implements Listener {

    @EventHandler
    public abstract void onEvent(T event);

}
