package com.envyful.api.forge.player;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.attribute.ForgeTrigger;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.manager.AbstractPlayerManager;
import com.envyful.api.player.name.NameStore;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.List;

/**
 *
 * Forge implementation of the {@link PlayerManager} interface.
 * Registers the {@link PlayerListener} class as a listener with forge on instantiation so that it can
 * automatically update the cache when player log in and out of the server.
 *
 * Simple instantiation as not enough arguments to warrant a builder class and
 */
public class ForgePlayerManager extends AbstractPlayerManager<ForgeEnvyPlayer, ServerPlayerEntity> {

    public ForgePlayerManager() {
        super(ServerPlayerEntity::getUUID);

        MinecraftForge.EVENT_BUS.register(new PlayerListener());
    }

    public ForgePlayerManager(NameStore nameStore) {
        this();

        this.nameStore = nameStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute<Y>, Y> void registerAttribute(AttributeBuilder<X, Y, ServerPlayerEntity> builder) {
        builder.triggers(
                ForgeTrigger.singleSet(PlayerEvent.PlayerLoggedInEvent.class, event -> this.cachedPlayers.get(event.getEntity().getUUID())),
                ForgeTrigger.singleSet(PlayerEvent.PlayerLoggedOutEvent.class, event -> this.cachedPlayers.get(event.getEntity().getUUID())),
                ForgeTrigger.asyncSave(WorldEvent.Save.class, event -> List.copyOf(this.cachedPlayers.values())),
                ForgeTrigger.asyncSave(FMLServerStoppingEvent.class, event -> List.copyOf(this.cachedPlayers.values()))
        );

        if (builder.offlineIdMapper() == null) {
            builder.offlineIdMapper(uuid -> (Y) uuid);
        }

        super.registerAttribute(builder);
    }

    private final class PlayerListener {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            var player = new ForgeEnvyPlayer((ServerPlayerEntity) event.getPlayer());
            cachedPlayers.put(event.getPlayer().getUUID(), player);

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
                ForgeEnvyPlayer player = cachedPlayers.get(event.getPlayer().getUUID());

                player.setParent((ServerPlayerEntity) event.getPlayer());
            }, 5L);
        }
    }
}
