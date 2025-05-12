package com.envyful.api.neoforge.player;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.manager.AbstractPlayerManager;
import com.envyful.api.player.name.NameStore;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * Forge implementation of the {@link PlayerManager} interface.
 * Registers the {@link PlayerListener} class as a listener with forge on instantiation so that it can
 * automatically update the cache when player log in and out of the server.
 *
 * Simple instantiation as not enough arguments to warrant a builder class and
 */
public class ForgePlayerManager extends AbstractPlayerManager<ForgeEnvyPlayer, ServerPlayer> {

    public ForgePlayerManager() {
        super(ServerPlayer::getUUID);

        NeoForge.EVENT_BUS.register(new PlayerListener());
    }

    public ForgePlayerManager(BiConsumer<UUID, Throwable> errorHandler) {
        super(errorHandler, ServerPlayer::getUUID);

        NeoForge.EVENT_BUS.register(new PlayerListener());
    }

    public ForgePlayerManager(NameStore nameStore) {
        this();

        this.nameStore = nameStore;
    }

    public ForgePlayerManager(BiConsumer<UUID, Throwable> errorHandler, NameStore nameStore) {
        this(errorHandler);

        this.nameStore = nameStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> void registerAttribute(AttributeBuilder<X, ForgeEnvyPlayer> builder) {
        builder.triggers(
                singleSet(PlayerEvent.PlayerLoggedInEvent.class, playerLoggedInEvent -> this.cachedPlayers.get(playerLoggedInEvent.getEntity().getUUID())),
                singleSave(PlayerEvent.PlayerLoggedOutEvent.class, playerLoggedInEvent -> this.cachedPlayers.get(playerLoggedInEvent.getEntity().getUUID())),
                save(LevelEvent.Save.class, event -> List.copyOf(this.cachedPlayers.values())),
                save(ServerStoppingEvent.class, event -> List.copyOf(this.cachedPlayers.values()))
        );

        super.registerAttribute(builder);
    }


    @Override
    protected <Y> void registerListeners(Class<Y> eventClass, Function<Y, List<ForgeEnvyPlayer>> converter, AttributeTrigger<ForgeEnvyPlayer> trigger) {
        this.registerGenericListeners(eventClass, converter, trigger);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Event> void registerGenericListeners(Class<?> eventClass, Function<?, List<ForgeEnvyPlayer>> converter, AttributeTrigger<ForgeEnvyPlayer> trigger) {
        registerNonGenericListeners((Class<Y>) eventClass, (Function<Y, List<ForgeEnvyPlayer>>) converter, trigger);
    }

    private <Y extends Event> void registerNonGenericListeners(Class<Y> eventClass, Function<Y, List<ForgeEnvyPlayer>> converter, AttributeTrigger<ForgeEnvyPlayer> trigger) {
        NeoForge.EVENT_BUS.addListener(eventClass, event -> {
            for (var holder : converter.apply((Y) event)) {
                if (holder != null) {
                    trigger.trigger(holder);
                }
            }
        });
    }

    private final class PlayerListener {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            var player = new ForgeEnvyPlayer((ServerPlayer) event.getEntity());
            cachedPlayers.put(event.getEntity().getUUID(), player);

            if (ForgePlayerManager.this.nameStore != null) {
                ForgePlayerManager.this.nameStore.updateStored(player.getUniqueId(), player.getName());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
            PlatformProxy.runLater(() -> cachedPlayers.remove(event.getEntity().getUUID()), 40);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            UtilConcurrency.runLater(() -> {
                var player = cachedPlayers.get(event.getEntity().getUUID());

                player.setParent((ServerPlayer) event.getEntity());
            }, 5L);
        }
    }
}
