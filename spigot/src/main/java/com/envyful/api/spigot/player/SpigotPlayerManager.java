package com.envyful.api.spigot.player;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.attribute.data.PlayerAttributeData;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.EmptySaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 *
 * Spigot implementation of the {@link PlayerManager} interface.
 * Registers the {@link PlayerListener} class as a listener with Spigot on instantiation so that it can
 * automatically update the cache when player log in and out of the server.
 *
 * Simple instantiation as not enough arguments to warrant a builder class and
 */
public class SpigotPlayerManager implements PlayerManager<SpigotEnvyPlayer, Player> {

    private final Map<UUID, SpigotEnvyPlayer> cachedPlayers = Maps.newHashMap();
    private final List<PlayerAttributeData> attributeData = Lists.newArrayList();

    private SaveManager<Player> saveManager = new EmptySaveManager<>(this);

    public SpigotPlayerManager(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), plugin);
    }

    @Override
    public SpigotEnvyPlayer getPlayer(Player player) {
        return this.getPlayer(player.getUniqueId());
    }

    @Override
    public SpigotEnvyPlayer getPlayer(UUID uuid) {
        return this.cachedPlayers.get(uuid);
    }

    @Override
    public SpigotEnvyPlayer getOnlinePlayer(String username) {
        for (SpigotEnvyPlayer online : this.cachedPlayers.values()) {
            if (online.getParent().getName().equals(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public SpigotEnvyPlayer getOnlinePlayerCaseInsensitive(String username) {
        for (SpigotEnvyPlayer online : this.cachedPlayers.values()) {
            if (online.getParent().getName().equalsIgnoreCase(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public List<SpigotEnvyPlayer> getOnlinePlayers() {
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
    public void registerAttribute(Class<? extends Attribute<?>> attribute) {
        this.attributeData.add(new PlayerAttributeData( this, attribute));

        if (this.saveManager != null) {
            this.saveManager.registerAttribute(attribute);
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
    public <A extends Attribute<B>, B> A loadAttribute(Class<? extends A> attributeClass, B id) {
        return this.saveManager.loadAttribute(attributeClass, id);
    }

    private final class PlayerListener implements Listener {

        private final SpigotPlayerManager manager;

        private PlayerListener(SpigotPlayerManager manager) {
            this.manager = manager;
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onAsyncPrePlayerLogin(AsyncPlayerPreLoginEvent event) {
            SpigotEnvyPlayer player = new SpigotEnvyPlayer(this.manager.saveManager,event.getUniqueId());
            this.manager.cachedPlayers.put(event.getUniqueId(), player);

            UtilConcurrency.runAsync(() -> {
                this.manager.saveManager.loadData(player).whenComplete((attributes, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
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

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerJoin(PlayerLoginEvent event) {
            this.manager.cachedPlayers.get(event.getPlayer().getUniqueId()).setParent(event.getPlayer());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerQuit(PlayerQuitEvent event) {
            SpigotEnvyPlayer player = this.manager.cachedPlayers.remove(event.getPlayer().getUniqueId());

            if (player == null) {
                return;
            }

            if (Bukkit.isStopping()) {
                this.saveData(player);
            } else {
                UtilConcurrency.runAsync(() -> this.saveData(player));
            }
        }

        private void saveData(SpigotEnvyPlayer player) {
            for (Attribute<?> value : player.getAttributes()) {
                if (value != null) {
                    this.manager.saveManager.saveData(player, value);
                }
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerRespawn(PlayerPostRespawnEvent event) {
            UtilConcurrency.runLater(() -> {
                SpigotEnvyPlayer player = this.manager.cachedPlayers.get(event.getPlayer().getUniqueId());

                player.setParent(event.getPlayer());
            }, 5L);
        }
    }
}
