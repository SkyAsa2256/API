package com.envyful.api.player.manager;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.name.NameStore;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.StandardSaveManager;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractPlayerManager<A extends EnvyPlayer<B>, B> implements PlayerManager<A, B> {

    protected final Map<UUID, A> cachedPlayers = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute<?>>, AttributeData<?, ?, B>> attributeData = new HashMap<>();
    protected final Function<B, UUID> uuidGetter;

    protected SaveManager<B> saveManager = new StandardSaveManager<>(this);
    protected NameStore nameStore = null;

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

    @Nullable
    @Override
    public NameStore getNameStore() {
        return this.nameStore;
    }

    @Override
    public void setNameStore(NameStore nameStore) {
        this.nameStore = nameStore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C, T extends Attribute<C>> C mapId(Class<T> attributeClass, UUID uuid) {
        var player = this.getPlayer(uuid);

        if (player != null && player.hasAttribute(attributeClass)) {
            var attribute = player.getAttributeNow(attributeClass);

            return attribute.getId();
        }

        var data = this.attributeData.get(attributeClass);

        if (data == null) {
            throw new IllegalArgumentException("No attribute data found for " + attributeClass.getSimpleName() + " " + this.attributeData.keySet().stream().map(Class::getSimpleName).collect(Collectors.joining(", ")));
        }

        return (C) data.offlineIdMapper().apply(uuid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute<Y>, Y> void registerAttribute(AttributeData<X, Y, B> attributeData) {
        for (var trigger : attributeData.triggers()) {
            trigger.addAttribute(attributeData);
        }

        this.attributeData.put(attributeData.attributeClass(), attributeData);

        if (this.saveManager != null) {
            this.saveManager.registerAttribute(attributeData);
        }
    }
}
