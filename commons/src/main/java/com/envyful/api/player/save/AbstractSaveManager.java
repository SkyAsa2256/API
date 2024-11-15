package com.envyful.api.player.save;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class AbstractSaveManager<T> implements SaveManager<T> {

    protected final Map<Class<? extends Attribute<?>>, PlayerManager.AttributeData<?, ?, T>> registeredAttributes = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute<?>>, Map<Object, Attribute<?>>> sharedAttributes = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute<?>>, String> attributeSaveModes = new ConcurrentHashMap<>();

    protected final PlayerManager<?, T> playerManager;
    protected BiConsumer<EnvyPlayer<T>, Throwable> errorHandler = (player, throwable) -> UtilLogger.logger().ifPresent(logger -> logger.error("Error loading data for " + player.getUniqueId() + " " + player.getName(), throwable));

    protected String saveMode = SQLDatabaseDetails.ID;

    protected AbstractSaveManager(PlayerManager<?, T> playerManager) {
        this(playerManager, null);
    }

    protected AbstractSaveManager(PlayerManager<?, T> playerManager, @Nullable BiConsumer<EnvyPlayer<T>, Throwable> errorHandler) {
        this.playerManager = playerManager;

        if (errorHandler != null) {
            this.errorHandler = errorHandler;
        }
    }

    @Override
    public BiConsumer<EnvyPlayer<T>, Throwable> getErrorHandler() {
        return this.errorHandler;
    }

    @Override
    public <A extends Attribute<B>, B> void registerAttribute(PlayerManager.AttributeData<A, B, T> attribute) {
        this.registeredAttributes.put(attribute.attributeClass(), attribute);
    }

    @SuppressWarnings("unchecked")
    protected <A> Attribute<A> getSharedAttribute(Class<? extends A> attributeClass, Object o) {
        return (Attribute<A>) this.sharedAttributes.computeIfAbsent((Class<? extends Attribute<?>>) attributeClass, ___ -> new HashMap<>()).get(o);
    }

    @SuppressWarnings("unchecked")
    protected void addSharedAttribute(Object key, Attribute<?> attribute) {
        this.sharedAttributes.computeIfAbsent((Class<? extends Attribute<?>>) attribute.getClass(), ___ -> new HashMap<>()).put(key, attribute);
    }

    @Override
    public String getSaveMode() {
        return this.saveMode;
    }

    @Override
    public void setSaveMode(String saveMode) {
        this.saveMode = saveMode;
    }

    @Override
    public <A extends Attribute<B>, B> void overrideSaveMode(Class<A> attributeClass, String saveMode) {
        this.attributeSaveModes.put(attributeClass, saveMode);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute<B>, B> A loadAttributeFromDataGeneric(PlayerManager.AttributeData<?, ?, ?> data, B id) {
        return this.loadAttributeFromData((PlayerManager.AttributeData<A, B, ?>) data, id);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute<B>, B> A loadAttributeFromData(PlayerManager.AttributeData<A, B, ?> data, B id) {
        var adapter = data.adapters().get(this.getSaveMode());
        var instance = data.constructor().apply(id);

        if (adapter == null) {
            if (instance instanceof AttributeAdapter) {
                ((AttributeAdapter<A, B>) instance).load(instance);
            }
        } else {
            adapter.load(instance);
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute<B>, B> void saveAttributeFromDataGeneric(PlayerManager.AttributeData<?, ?, ?> data, A attribute) {
        this.saveAttributeFromData((PlayerManager.AttributeData<A, B, ?>) data, attribute);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute<B>, B> void saveAttributeFromData(PlayerManager.AttributeData<A, B, ?> data, A attribute) {
        var adapter = this.getAdapter(data);
        if (adapter == null) {
            if (attribute instanceof AttributeAdapter) {
                ((AttributeAdapter<A, B>) attribute).save(attribute);
            }
        } else {
            adapter.save(attribute);
        }
    }

    protected <A extends Attribute<B>, B> AttributeAdapter<A, B> getAdapter(PlayerManager.AttributeData<A, B, ?> data) {
        var saveMode = this.attributeSaveModes.getOrDefault(data.attributeClass(), this.getSaveMode());
        return data.adapters().get(saveMode.toLowerCase(Locale.ROOT));
    }
}
