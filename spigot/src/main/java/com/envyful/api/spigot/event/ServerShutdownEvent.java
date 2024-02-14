package com.envyful.api.spigot.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Event to fire player attribute saving when the server is shutting down
 * <br>
 * This is a custom event, that must be fired by plugins, because Spigot/Bukkit/Paper
 * do not have a built-in event for server shutdown.
 *
 */
public class ServerShutdownEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     *
     * Gets the handler list for the event
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
