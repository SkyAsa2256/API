package com.envyful.api.velocity.player;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.attribute.data.PlayerAttributeData;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.EmptySaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 *
 * Velocity implementation of the {@link PlayerManager} interface.
 * Registers the {@link PlayerListener} class as a listener with Velocity on instantiation so that it can
 * automatically update the cache when player log in and out of the server.
 *
 * Simple instantiation as not enough arguments to warrant a builder class and
 */
public class VelocityPlayerManager implements PlayerManager<VelocityEnvyPlayer, Player> {

    private final Map<UUID, VelocityEnvyPlayer> cachedPlayers = Maps.newHashMap();
    private final List<PlayerAttributeData> attributeData = Lists.newArrayList();

    private SaveManager<Player> saveManager = new EmptySaveManager<>(this);
    private ProxyServer proxyServer;

    public VelocityPlayerManager(Object plugin, ProxyServer proxy) {
        this.proxyServer = proxy;
        proxy.getEventManager().register(plugin, new PlayerListener(this));
    }

    @Override
    public VelocityEnvyPlayer getPlayer(Player player) {
        return this.getPlayer(player.getUniqueId());
    }

    @Override
    public VelocityEnvyPlayer getPlayer(UUID uuid) {
        return this.cachedPlayers.get(uuid);
    }

    @Override
    public VelocityEnvyPlayer getOnlinePlayer(String username) {
        for (VelocityEnvyPlayer online : this.cachedPlayers.values()) {
            if (online.getParent().getUsername().equals(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public VelocityEnvyPlayer getOnlinePlayerCaseInsensitive(String username) {
        for (VelocityEnvyPlayer online : this.cachedPlayers.values()) {
            if (online.getParent().getUsername().equalsIgnoreCase(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public List<VelocityEnvyPlayer> getOnlinePlayers() {
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
    public void setSaveManager(SaveManager<Player> saveManager) {
        this.saveManager = saveManager;
    }

    @Override
    public SaveManager<Player> getSaveManager() {
        return this.saveManager;
    }

    @Override
    public <A extends Attribute<B>, B> CompletableFuture<A> loadAttribute(Class<? extends A> attributeClass, B id) {
        return this.saveManager.loadAttribute(attributeClass, id);
    }

    private static final class PlayerListener {

        private final VelocityPlayerManager manager;

        private PlayerListener(VelocityPlayerManager manager) {
            this.manager = manager;
        }

        @Subscribe(order = PostOrder.LAST)
        public void onAsyncPrePlayerLogin(LoginEvent event) {
            VelocityEnvyPlayer player = new VelocityEnvyPlayer(this.manager.saveManager,
                    this.manager.proxyServer, event.getPlayer().getUniqueId());
            player.setParent(event.getPlayer());
            this.manager.cachedPlayers.put(event.getPlayer().getUniqueId(), player);

            UtilConcurrency.runAsync(() -> {
                this.manager.saveManager.loadData(player).whenComplete((attributes, throwable) -> {
                    if (throwable != null) {
                        this.manager.saveManager.getErrorHandler().accept(player, throwable);
                        return;
                    }

                    for (PlayerAttributeData attributeDatum : this.manager.attributeData) {
                        Attribute<?> attribute = this.findAttribute(attributeDatum, attributes);

                        if (attribute == null) {
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

        @Subscribe(order = PostOrder.LAST)
        public void onPlayerQuit(DisconnectEvent event) {
            VelocityEnvyPlayer player = this.manager.cachedPlayers.remove(event.getPlayer().getUniqueId());

            if (player == null) {
                return;
            }

            for (Attribute<?> value : player.getAttributes()) {
                if (value != null) {
                    this.manager.saveManager.saveData(player, value);
                }
            }
        }
    }
}
