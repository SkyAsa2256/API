package com.envyful.api.player.manager;

import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.player.name.NameStore;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.impl.StandardSaveManager;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractPlayerManager<A extends EnvyPlayer<B>, B> implements PlayerManager<A, B> {

    protected final Map<UUID, A> cachedPlayers = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute>, AttributeData<?, A>> attributeData = new HashMap<>();
    protected final Function<B, UUID> uuidGetter;

    protected SaveManager<A> saveManager = new StandardSaveManager<>();
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
        return List.copyOf(this.cachedPlayers.values());
    }

    @Override
    public void setSaveManager(SaveManager<A> saveManager) {
        this.saveManager = saveManager;
    }

    @Override
    public SaveManager<A> getSaveManager() {
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
    public <T extends Attribute> UUID mapId(Class<T> attributeClass, UUID uuid) {
        var player = this.getPlayer(uuid);

        if (player != null && player.hasAttribute(attributeClass)) {
            var attribute = player.getAttributeNow(attributeClass);

            return attribute.getId();
        }

        var data = this.attributeData.get(attributeClass);

        if (data == null) {
            throw new IllegalArgumentException("No attribute data found for " + attributeClass.getSimpleName() + " " + this.attributeData.keySet().stream().map(Class::getSimpleName).collect(Collectors.joining(", ")));
        }

        return data.offlineIdMapper().apply(uuid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> void registerAttribute(AttributeData<X, A> attributeData) {
        for (var trigger : attributeData.triggers()) {
            trigger.addAttribute(attributeData);
        }

        this.attributeData.put(attributeData.attributeClass(), attributeData);

        if (this.saveManager != null) {
            this.saveManager.registerAttribute(attributeData);
        }
    }
}
