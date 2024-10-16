package com.envyful.api.forge.player.attribute;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.trigger.ClearAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SaveAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SetAttributeTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Static helper class for creating triggers for Forge
 *
 */
public class ForgeTrigger {

    private ForgeTrigger() {
        throw new UnsupportedOperationException("Cannot instantiate a static class");
    }


    /**
     *
     * Creates a trigger to set the attribute for a single player
     *
     * @param event the event
     * @param converter the converter to convert the event to a player
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> singleSet(Class<A> event, Function<A, ForgeEnvyPlayer> converter) {
        return singleSet(MinecraftForge.EVENT_BUS, event, converter);
    }

    /**
     *
     * Creates a trigger to set the attribute for a single player
     *
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a player
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> singleSet(IEventBus eventBus, Class<A> event, Function<A, ForgeEnvyPlayer> converter) {
        return set(eventBus, event, a -> {
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
     * @param event the event
     * @param converter the converter to convert the event to a list of players
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> set(Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        return set(MinecraftForge.EVENT_BUS, event, converter);
    }

    /**
     *
     * Creates a trigger to set the attribute for multiple players
     *
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a list of players
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> set(IEventBus eventBus, Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        var trigger = new SetAttributeTrigger<ServerPlayer>();
        createHandler(eventBus, event, converter, trigger::trigger);
        return trigger;
    }

    /**
     *
     * Creates a trigger to set the attribute for multiple players
     *
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a list of players
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> asyncSet(IEventBus eventBus, Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        var trigger = new SetAttributeTrigger<ServerPlayer>();
        createAsyncHandler(eventBus, event, converter, trigger::trigger);
        return trigger;
    }

    /**
     *
     * Creates a trigger to clear the attribute for a single player
     *
     * @param event the event
     * @param converter the converter to convert the event to a player
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> singleClear(Class<A> event, Function<A, ForgeEnvyPlayer> converter) {
        return singleClear(MinecraftForge.EVENT_BUS, event, converter);
    }

    /**
     *
     * Creates a trigger to clear the attribute for a single player
     *
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a player
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> singleClear(IEventBus eventBus, Class<A> event, Function<A, ForgeEnvyPlayer> converter) {
        return clear(eventBus, event, a -> {
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
     * @param event the event
     * @param converter the converter to convert the event to a list of players
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> clear(Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        return clear(MinecraftForge.EVENT_BUS, event, converter);
    }

    /**
     *
     * Creates a trigger to clear the attribute for multiple players
     *
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a list of players
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> clear(IEventBus eventBus, Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        var trigger = new ClearAttributeTrigger<ServerPlayer>();
        createHandler(eventBus, event, converter, trigger::trigger);
        return trigger;
    }

    /**
     *
     * Creates a trigger to save the attribute data for a single player
     *
     * @param event the event
     * @param converter the converter to convert the event to a player
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> singleSave(Class<A> event, Function<A, ForgeEnvyPlayer> converter) {
        return save(MinecraftForge.EVENT_BUS, event, a -> {
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
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a player
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> singleSave(IEventBus eventBus, Class<A> event, Function<A, ForgeEnvyPlayer> converter) {
        return save(eventBus, event, a -> {
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
     * @param event the event
     * @param converter the converter to convert the event to a list of players
     * @return the trigger
     * @param <A> the event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> save(Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        return save(MinecraftForge.EVENT_BUS, event, converter);
    }

    /**
     *
     * Creates a trigger to save the attribute data for multiple players
     *
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a list of players
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> save(IEventBus eventBus, Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        var trigger = new SaveAttributeTrigger<ServerPlayer>();
        createHandler(eventBus, event, converter, trigger::trigger);
        return trigger;
    }

    /**
     *
     * Creates a trigger to save the attribute data for multiple players
     *
     * @param eventBus The event bus
     * @param event The event
     * @param converter The converter to convert the event to a list of players
     * @return The trigger
     * @param <A> The event type
     */
    public static <A extends Event> AttributeTrigger<ServerPlayer> asyncSave(IEventBus eventBus, Class<A> event, Function<A, List<ForgeEnvyPlayer>> converter) {
        var trigger = new SaveAttributeTrigger<ServerPlayer>();
        createAsyncHandler(eventBus, event, converter, trigger::trigger);
        return trigger;
    }

    @SuppressWarnings("unchecked")
    private static <A extends Event> void createHandler(IEventBus eventBus, Class<A> eventClass, Function<A, List<ForgeEnvyPlayer>> converter, Consumer<EnvyPlayer<ServerPlayer>> trigger) {
        eventBus.addGenericListener(eventClass, event -> {
            for (var player : converter.apply((A) event)) {
                if (player != null) {
                    trigger.accept(player);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <A extends Event> void createAsyncHandler(IEventBus eventBus, Class<A> eventClass, Function<A, List<ForgeEnvyPlayer>> converter, Consumer<EnvyPlayer<ServerPlayer>> trigger) {
        eventBus.addGenericListener(eventClass, event -> {
            if (!eventClass.isAssignableFrom(event.getClass())) {
                return;
            }

            UtilConcurrency.runAsync(() -> {
                for (var player : converter.apply((A) event)) {
                    if (player != null) {
                        trigger.accept(player);
                    }
                }
            });
        });
    }
}
