package com.envyful.api.forge.player;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.attribute.data.PlayerAttributeData;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.EmptySaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 *
 * Forge implementation of the {@link PlayerManager} interface.
 * Registers the {@link PlayerListener} class as a listener with forge on instantiation so that it can
 * automatically update the cache when player log in and out of the server.
 *
 * Simple instantiation as not enough arguments to warrant a builder class and
 */
public class ForgePlayerManager implements PlayerManager<ForgeEnvyPlayer, ServerPlayerEntity> {

    private final Map<UUID, ForgeEnvyPlayer> cachedPlayers = Maps.newHashMap();
    private final List<PlayerAttributeData> attributeData = Lists.newArrayList();

    private SaveManager<ServerPlayerEntity> saveManager = new EmptySaveManager<>(this);

    public ForgePlayerManager() {
        MinecraftForge.EVENT_BUS.register(new PlayerListener(this));
        UsernameFactory.init();
    }

    @Override
    public ForgeEnvyPlayer getPlayer(ServerPlayerEntity player) {
        return this.getPlayer(player.getUUID());
    }

    @Override
    public ForgeEnvyPlayer getPlayer(UUID uuid) {
        return this.cachedPlayers.get(uuid);
    }

    @Override
    public ForgeEnvyPlayer getOnlinePlayer(String username) {
        for (ForgeEnvyPlayer online : this.cachedPlayers.values()) {
            if (online.getParent().getName().getString().equals(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public ForgeEnvyPlayer getOnlinePlayerCaseInsensitive(String username) {
        for (ForgeEnvyPlayer online : this.cachedPlayers.values()) {
            if (online.getParent().getName().getString().equalsIgnoreCase(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public List<ForgeEnvyPlayer> getOnlinePlayers() {
        return Collections.unmodifiableList(Lists.newArrayList(this.cachedPlayers.values()));
    }

    @Override
    public List<Attribute<?>> getOfflineAttributes(UUID uuid) {
        try {
            return this.saveManager.loadData(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public <A extends Attribute<B>, B> void registerAttribute(Class<A> attribute, Supplier<A> constructor) {
        this.attributeData.add(new PlayerAttributeData(attribute));

        if (this.saveManager != null) {
            this.saveManager.registerAttribute(attribute, constructor);
        }
    }

    @Override
    public void setSaveManager(SaveManager<ServerPlayerEntity> saveManager) {
        this.saveManager = saveManager;
    }

    @Override
    public SaveManager<ServerPlayerEntity> getSaveManager() {
        return this.saveManager;
    }

    @Override
    public <A extends Attribute<B>, B> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, B id) {
        return this.saveManager.loadAttribute(attributeClass, id);
    }

    private static final class PlayerListener {

        private final ForgePlayerManager manager;
        private long lastSave = -1L;

        private PlayerListener(ForgePlayerManager manager) {
            this.manager = manager;
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            ForgeEnvyPlayer player = new ForgeEnvyPlayer(this.manager.saveManager,
                    (ServerPlayerEntity) event.getPlayer());
            this.manager.cachedPlayers.put(event.getPlayer().getUUID(), player);

            UtilConcurrency.runAsync(() -> {
                this.manager.saveManager.loadData(player).whenComplete((attributes, throwable) -> {
                    if (throwable != null) {
                        this.manager.saveManager.getErrorHandler().accept(player, throwable);
                        return;
                    }

                    for (PlayerAttributeData attributeDatum : this.manager.attributeData) {
                        Attribute<?> attribute = this.findAttribute(attributeDatum, attributes);

                        if (attribute == null) {
                            UtilLogger.logger().ifPresent(logger -> logger.error("Null attribute loaded for {}", attributeDatum.getAttributeClass().getName()));
                            continue;
                        }

                        player.setAttribute(attribute);
                    }
                });
            });
        }

        private Attribute<?> findAttribute(PlayerAttributeData attributeDatum,
                                                 List<Attribute<?>> playerAttributes) {
            for (Attribute<?> playerAttribute : playerAttributes) {
                if (Objects.equals(attributeDatum.getAttributeClass(), playerAttribute.getClass())) {
                    return playerAttribute;
                }
            }

            return null;
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
            ForgeEnvyPlayer player = this.manager.cachedPlayers.remove(event.getPlayer().getUUID());

            if (player == null) {
                return;
            }

            UtilConcurrency.runAsync(() -> {
                for (Attribute<?> value : player.getAttributes()) {
                    if (value != null) {
                        this.manager.saveManager.saveData(player, value);
                    }
                }
            });
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event) {
            if (!this.shouldSave()) {
                return;
            }

            this.lastSave = System.currentTimeMillis();

            UtilConcurrency.runAsync(() -> {
                for (ForgeEnvyPlayer onlinePlayer : this.manager.getOnlinePlayers()) {
                    for (Attribute<?> value : onlinePlayer.getAttributes()) {
                        if (value != null) {
                            this.manager.saveManager.saveData(onlinePlayer, value);
                        }
                    }
                }
            });
        }

        private boolean shouldSave() {
            return this.lastSave == -1 || (System.currentTimeMillis() - this.lastSave) >= TimeUnit.MINUTES.toMillis(2);
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            UtilConcurrency.runLater(() -> {
                ForgeEnvyPlayer player = this.manager.cachedPlayers.get(event.getPlayer().getUUID());

                player.setParent((ServerPlayerEntity) event.getPlayer());
            }, 5L);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onPreServerShutdown(FMLServerStoppingEvent event) {
            UtilConcurrency.runAsync(() -> {
                for (ForgeEnvyPlayer player : this.manager.cachedPlayers.values()) {
                    for (Attribute<?> value : player.getAttributes()) {
                        if (value != null) {
                            this.manager.saveManager.saveData(player, value);
                        }
                    }
                }
            });
        }
    }
}
