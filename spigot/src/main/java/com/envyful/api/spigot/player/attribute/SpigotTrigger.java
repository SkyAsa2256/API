package com.envyful.api.spigot.player.attribute;

import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.trigger.ClearAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SaveAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SetAttributeTrigger;
import com.envyful.api.spigot.listener.GenericListener;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Static helper class for creating triggers for Spigot
 *
 */
public class SpigotTrigger {

    private static final SetAttributeTrigger<SpigotEnvyPlayer> SET_ATTRIBUTE_TRIGGER = new SetAttributeTrigger<>();
    private static final ClearAttributeTrigger<SpigotEnvyPlayer> CLEAR_ATTRIBUTE_TRIGGER = new ClearAttributeTrigger<>();
    private static final SaveAttributeTrigger<SpigotEnvyPlayer> SAVE_ATTRIBUTE_TRIGGER = new SaveAttributeTrigger<>();

    private SpigotTrigger() {
        throw new UnsupportedOperationException("Cannot instantiate a static class");
    }

    /**
     *
     * Creates a trigger to set the attribute for a single player
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a player
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> singleSet(Plugin plugin, Class<A> event, Function<A, SpigotEnvyPlayer> converter) {
        return set(plugin, event, a -> {
            var player = converter.apply(a);

            if (player == null) {
                return List.of();
            }

            return List.of(player);
        });
    }

    /**
     *
     * Creates a trigger to set the attribute for multiple players
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a list of players
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> set(Plugin plugin, Class<A> event, Function<A, List<SpigotEnvyPlayer>> converter) {
        createHandler(plugin, event, converter, SET_ATTRIBUTE_TRIGGER::trigger);
        return SET_ATTRIBUTE_TRIGGER;
    }

    /**
     *
     * Creates a trigger to clear the attribute for a single player
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a player
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> singleClear(Plugin plugin, Class<A> event, Function<A, SpigotEnvyPlayer> converter) {
        return clear(plugin, event, a -> {
            var player = converter.apply(a);

            if (player == null) {
                return List.of();
            }

            return List.of(player);
        });
    }

    /**
     *
     * Creates a trigger to clear the attribute for multiple players
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a list of players
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> clear(Plugin plugin, Class<A> event, Function<A, List<SpigotEnvyPlayer>> converter) {
        createHandler(plugin, event, converter, CLEAR_ATTRIBUTE_TRIGGER::trigger);
        return CLEAR_ATTRIBUTE_TRIGGER;
    }

    /**
     *
     * Creates a trigger to save the attribute data for a single player
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a player
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> singleSave(Plugin plugin, Class<A> event, Function<A, SpigotEnvyPlayer> converter) {
        return save(plugin, event, a -> {
            var player = converter.apply(a);

            if (player == null) {
                return List.of();
            }

            return List.of(player);
        });
    }

    /**
     *
     * Creates a trigger to save the attribute data for a single player
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a player
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> singleAsyncSave(Plugin plugin, Class<A> event, Function<A, SpigotEnvyPlayer> converter) {
        return asyncSave(plugin, event, a -> {
            var player = converter.apply(a);

            if (player == null) {
                return List.of();
            }

            return List.of(player);
        });
    }

    /**
     *
     * Creates a trigger to save the attribute data for multiple players
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a list of players
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> save(Plugin plugin, Class<A> event, Function<A, List<SpigotEnvyPlayer>> converter) {
        createHandler(plugin, event, converter, SAVE_ATTRIBUTE_TRIGGER::trigger);
        return SAVE_ATTRIBUTE_TRIGGER;
    }

    /**
     *
     * Creates a trigger to save the attribute data for multiple players
     *
     * @param plugin the plugin
     * @param event the event
     * @param converter the converter to convert the event to a list of players
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<SpigotEnvyPlayer> asyncSave(Plugin plugin, Class<A> event, Function<A, List<SpigotEnvyPlayer>> converter) {
        createAsyncHandler(plugin, event, converter, SAVE_ATTRIBUTE_TRIGGER::trigger);
        return SAVE_ATTRIBUTE_TRIGGER;
    }

    @SuppressWarnings("unchecked")
    private static <A extends Event> void createHandler(Plugin plugin, Class<A> eventClass, Function<A, List<SpigotEnvyPlayer>> converter, Consumer<SpigotEnvyPlayer> trigger) {
        var listener = new GenericListener<A>() {
            @Override
            public void onEvent(A event) {
                // Spigot requires this but we don't really need to do anything here
            }
        };

        Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.NORMAL, (listener1, event) -> {
            if (!(listener1 instanceof GenericListener<?>) || !eventClass.isAssignableFrom(event.getClass())) {
                return;
            }

            for (var player : converter.apply((A) event)) {
                if (player != null) {
                    trigger.accept(player);
                }
            }
        }, plugin);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Event> void createAsyncHandler(Plugin plugin, Class<A> eventClass, Function<A, List<SpigotEnvyPlayer>> converter, Consumer<SpigotEnvyPlayer> trigger) {
        var listener = new GenericListener<A>() {
            @Override
            public void onEvent(A event) {
                // Spigot requires this but we don't really need to do anything here
            }
        };

        Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.NORMAL, (listener1, event) -> {
            if (!(listener1 instanceof GenericListener<?>) || !eventClass.isAssignableFrom(event.getClass())) {
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (var player : converter.apply((A) event)) {
                    if (player != null) {
                        trigger.accept(player);
                    }
                }
            });
        }, plugin);
    }
}
