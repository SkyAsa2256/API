package com.envyful.api.velocity.player;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.AttributeBuilder;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.manager.AbstractPlayerManager;
import com.envyful.api.player.name.NameStore;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 *
 * Velocity implementation of the {@link PlayerManager} interface.
 * Registers the {@link PlayerListener} class as a listener with Velocity on instantiation so that it can
 * automatically update the cache when player log in and out of the server.
 *
 * Simple instantiation as not enough arguments to warrant a builder class and
 */
public class VelocityPlayerManager extends AbstractPlayerManager<VelocityEnvyPlayer, Player> {

    private final ProxyServer proxyServer;
    private final Object plugin;

    public VelocityPlayerManager(Object plugin, ProxyServer proxy) {
        super(Player::getUniqueId);

        this.proxyServer = proxy;
        this.plugin = plugin;

        proxy.getEventManager().register(plugin, new PlayerListener());
    }

    public VelocityPlayerManager(Object plugin, ProxyServer proxy, NameStore nameStore) {
        this(plugin, proxy);

        this.nameStore = nameStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> void registerAttribute(AttributeBuilder<X, VelocityEnvyPlayer> builder) {
        builder.triggers(
                this.singleSet(LoginEvent.class, event -> this.cachedPlayers.get(event.getPlayer().getUniqueId())),
                this.singleSave(DisconnectEvent.class, event -> this.cachedPlayers.get(event.getPlayer().getUniqueId()))
        );

        super.registerAttribute(builder);
    }

    @Override
    protected <Y> void registerListeners(Class<Y> eventClass, Function<Y, List<VelocityEnvyPlayer>> converter, AttributeTrigger<VelocityEnvyPlayer> trigger) {
        this.proxyServer.getEventManager().register(plugin, eventClass, PostOrder.LAST, event -> {
            if (!eventClass.isAssignableFrom(event.getClass())) {
                return;
            }

            for (var player : converter.apply(event)) {
                if (player != null) {
                    trigger.trigger(player);
                }
            }
        });
    }

    private final class PlayerListener {

        @Subscribe(order = PostOrder.FIRST)
        public void onAsyncPrePlayerLogin(LoginEvent event) {
            VelocityEnvyPlayer player = new VelocityEnvyPlayer(proxyServer, event.getPlayer().getUniqueId());
            player.setParent(event.getPlayer());
            cachedPlayers.put(event.getPlayer().getUniqueId(), player);


            if (VelocityPlayerManager.this.nameStore != null) {
                VelocityPlayerManager.this.nameStore.updateStored(player.getUniqueId(), player.getName());
            }
        }

        @Subscribe(order = PostOrder.LAST)
        public void onPlayerQuit(DisconnectEvent event) {
            proxyServer.getScheduler().buildTask(plugin, () -> cachedPlayers.remove(event.getPlayer().getUniqueId()))
                    .delay(4, TimeUnit.SECONDS)
                    .schedule();
        }
    }
}
