package com.envyful.api.spigot.player;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.manager.AbstractPlayerManager;
import com.envyful.api.player.name.NameStore;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.StandardSaveManager;
import com.envyful.api.spigot.event.ServerShutdownEvent;
import com.envyful.api.spigot.player.attribute.SpigotTrigger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 *
 * Spigot implementation of the {@link PlayerManager} interface.
 * Registers the {@link PlayerListener} class as a listener with Spigot on instantiation so that it can
 * automatically update the cache when player log in and out of the server.
 *
 * Simple instantiation as not enough arguments to warrant a builder class and
 */
public class SpigotPlayerManager extends AbstractPlayerManager<SpigotEnvyPlayer, Player> {

    protected final Plugin plugin;

    public SpigotPlayerManager(Plugin plugin) {
        this(new StandardSaveManager<>(), plugin);
    }

    public SpigotPlayerManager(SaveManager<SpigotEnvyPlayer> saveManager, Plugin plugin) {
        super(saveManager, Player::getUniqueId);

        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    public SpigotPlayerManager(Plugin plugin, NameStore nameStore) {
        this(plugin );

        this.nameStore = nameStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> void registerAttribute(AttributeBuilder<X, SpigotEnvyPlayer> builder) {
        builder.triggers(
                SpigotTrigger.singleSet(this.plugin, AsyncPlayerPreLoginEvent.class, event -> this.cachedPlayers.get(event.getUniqueId())),
                SpigotTrigger.singleAsyncSave(this.plugin, PlayerQuitEvent.class, event -> this.cachedPlayers.get(event.getPlayer().getUniqueId())),
                SpigotTrigger.asyncSave(this.plugin, WorldSaveEvent.class, event -> List.copyOf(this.cachedPlayers.values())),
                SpigotTrigger.asyncSave(this.plugin, ServerShutdownEvent.class, event -> List.copyOf(this.cachedPlayers.values()))
        );

        super.registerAttribute(builder);
    }

    private final class PlayerListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onAsyncPrePlayerLogin(AsyncPlayerPreLoginEvent event) {
            var player = new SpigotEnvyPlayer(event.getUniqueId());
            cachedPlayers.put(event.getUniqueId(), player);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerJoin(PlayerLoginEvent event) {
            cachedPlayers.get(event.getPlayer().getUniqueId()).setParent(event.getPlayer());

            if (SpigotPlayerManager.this.nameStore != null) {
                SpigotPlayerManager.this.nameStore.updateStored(event.getPlayer().getUniqueId(), event.getPlayer().getName());
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> cachedPlayers.remove(event.getPlayer().getUniqueId()), 40L);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerRespawn(PlayerPostRespawnEvent event) {
            UtilConcurrency.runLater(() -> {
                SpigotEnvyPlayer player = cachedPlayers.get(event.getPlayer().getUniqueId());

                player.setParent(event.getPlayer());
            }, 5L);
        }
    }
}
