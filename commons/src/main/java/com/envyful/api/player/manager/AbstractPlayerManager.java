package com.envyful.api.player.manager;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.StandardSaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class AbstractPlayerManager<A extends EnvyPlayer<B>, B> implements PlayerManager<A, B> {

    protected final Map<UUID, A> cachedPlayers = Maps.newConcurrentMap();
    protected final Map<Class<? extends Attribute<?>>, AttributeData<?, ?, B>> attributeData = Maps.newHashMap();
    protected final Function<B, UUID> uuidGetter;

    protected SaveManager<B> saveManager = new StandardSaveManager<>(this);

    protected AbstractPlayerManager(Function<B, UUID> uuidGetter) {
        this.uuidGetter = uuidGetter;
    }

    @Override
    public A getPlayer(B player) {
        if (player == null) {
            return null;
        }

        return this.getPlayer(this.uuidGetter.apply(player));
    }

    @Override
    public A getPlayer(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return this.cachedPlayers.get(uuid);
    }

    @Override
    public A getOnlinePlayer(String username) {
        for (A online : this.cachedPlayers.values()) {
            if (online.getName().equals(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public A getOnlinePlayerCaseInsensitive(String username) {
        for (A online : this.cachedPlayers.values()) {
            if (online.getName().equalsIgnoreCase(username)) {
                return online;
            }
        }

        return null;
    }

    @Override
    public List<A> getOnlinePlayers() {
        return Collections.unmodifiableList(Lists.newArrayList(this.cachedPlayers.values()));
    }

    @Override
    public void setSaveManager(SaveManager<B> saveManager) {
        this.saveManager = saveManager;
    }

    @Override
    public SaveManager<B> getSaveManager() {
        return this.saveManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute<Y>, Y> void registerAttribute(AttributeData<X, Y, B> attributeData) {
        for (var trigger : attributeData.triggers()) {
            trigger.addAttribute(attributeData);
        }

        this.attributeData.put((Class<? extends Attribute<?>>) attributeData.getClass(), attributeData);

        if (this.saveManager != null) {
            this.saveManager.registerAttribute(attributeData);
        }
    }
}
