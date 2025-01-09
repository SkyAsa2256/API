package com.envyful.api.forge.player;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.attribute.ForgeTrigger;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.manager.AbstractPlayerManager;
import com.envyful.api.player.name.NameStore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

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

        MinecraftForge.EVENT_BUS.register(new PlayerListener());
    }

    public ForgePlayerManager(BiConsumer<UUID, Throwable> errorHandler) {
        super(errorHandler, ServerPlayer::getUUID);

        MinecraftForge.EVENT_BUS.register(new PlayerListener());
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
                ForgeTrigger.singleSet(PlayerEvent.PlayerLoggedInEvent.class, event -> this.cachedPlayers.get(event.getEntity().getUUID())),
                ForgeTrigger.asyncSingleSaveAndClear(PlayerEvent.PlayerLoggedOutEvent.class, event -> this.cachedPlayers.get(event.getEntity().getUUID())),
                ForgeTrigger.asyncSave(LevelEvent.Save.class, event -> List.copyOf(this.cachedPlayers.values())),
                ForgeTrigger.asyncSave(ServerStoppingEvent.class, event -> List.copyOf(this.cachedPlayers.values()))
        );

        super.registerAttribute(builder);
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
            UtilForgeConcurrency.runLater(() -> cachedPlayers.remove(event.getEntity().getUUID()), 40);
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
