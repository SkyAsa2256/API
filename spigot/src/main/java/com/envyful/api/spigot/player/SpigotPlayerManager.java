package com.envyful.api.spigot.player;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.manager.AbstractPlayerManager;
import com.envyful.api.player.name.NameStore;
import com.envyful.api.spigot.event.ServerShutdownEvent;
import com.envyful.api.spigot.listener.GenericListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

    public SpigotPlayerManager(Plugin plugin, BiConsumer<UUID, Throwable> errorHandler) {
        super(errorHandler, Player::getUniqueId);

        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    public SpigotPlayerManager(Plugin plugin) {
        super(Player::getUniqueId);

        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    public SpigotPlayerManager(Plugin plugin, NameStore nameStore) {
        this(plugin);

        this.nameStore = nameStore;
    }

    public SpigotPlayerManager(Plugin plugin, NameStore nameStore, BiConsumer<UUID, Throwable> errorHandler) {
        this(plugin, errorHandler);

        this.nameStore = nameStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> void registerAttribute(AttributeBuilder<X, SpigotEnvyPlayer> builder) {
        builder.triggers(
                singleSet(AsyncPlayerPreLoginEvent.class, playerLoggedInEvent -> this.cachedPlayers.get(playerLoggedInEvent.getUniqueId())),
                singleSave(PlayerQuitEvent.class, playerLoggedInEvent -> this.cachedPlayers.get(playerLoggedInEvent.getPlayer().getUniqueId())),
                save(WorldSaveEvent.class, event -> List.copyOf(this.cachedPlayers.values())),
                save(ServerShutdownEvent.class, event -> List.copyOf(this.cachedPlayers.values()))
        );

        super.registerAttribute(builder);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <Y> void registerListeners(Class<Y> eventClass, Function<Y, List<SpigotEnvyPlayer>> converter, AttributeTrigger<SpigotEnvyPlayer> trigger) {
        var listener = new GenericListener<>() {
            @Override
            public void onEvent(Event event) {
                // Spigot requires this but we don't really need to do anything here
            }
        };

        Bukkit.getPluginManager().registerEvent((Class<? extends Event>) eventClass, listener, EventPriority.NORMAL, (listener1, event) -> {
            if (!(listener1 instanceof GenericListener<?>) || !eventClass.isAssignableFrom(event.getClass())) {
                return;
            }

            for (var attributeHolder : converter.apply((Y) event)) {
                if (attributeHolder != null) {
                    trigger.trigger(attributeHolder);
                }
            }
        }, plugin);
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
