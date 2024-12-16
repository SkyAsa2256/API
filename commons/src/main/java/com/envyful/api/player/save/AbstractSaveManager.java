package com.envyful.api.player.save;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.api.player.attribute.data.AttributeData;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class AbstractSaveManager<T extends AttributeHolder> implements SaveManager<T> {

    protected final Map<Class<? extends Attribute>, AttributeData<?, T>> registeredAttributes = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute>, Map<Object, Attribute>> sharedAttributes = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute>, String> attributeSaveModes = new ConcurrentHashMap<>();

    protected BiConsumer<T, Throwable> errorHandler = (player, throwable) -> UtilLogger.logger().ifPresent(logger -> logger.error("Error loading data for " + player.getUniqueId() + " " + player.getName(), throwable));

    protected String saveMode = SQLDatabaseDetails.ID;

    protected AbstractSaveManager() {
        this(null);
    }

    protected AbstractSaveManager(@Nullable BiConsumer<T, Throwable> errorHandler) {
        if (errorHandler != null) {
            this.errorHandler = errorHandler;
        }
    }

    @Override
    public BiConsumer<T, Throwable> getErrorHandler() {
        return this.errorHandler;
    }

    @Override
    public <A extends Attribute> void registerAttribute(AttributeData<A, T> attribute) {
        this.registeredAttributes.put(attribute.attributeClass(), attribute);
    }

    @SuppressWarnings("unchecked")
    protected Attribute getSharedAttribute(Class<?> attributeClass, Object o) {
        return this.sharedAttributes.computeIfAbsent((Class<? extends Attribute>) attributeClass, ___ -> new HashMap<>()).get(o);
    }

    @SuppressWarnings("unchecked")
    protected void addSharedAttribute(Object key, Attribute attribute) {
        this.sharedAttributes.computeIfAbsent(attribute.getClass(), ___ -> new HashMap<>()).put(key, attribute);
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
    public <A extends Attribute> String getSaveMode(Class<A> attributeClass) {
        return this.attributeSaveModes.getOrDefault(attributeClass, this.saveMode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute> AttributeAdapter<A> getAdapter(Class<A> attributeClass) {
        var data = this.registeredAttributes.get(attributeClass);
        return (AttributeAdapter<A>) this.getAdapter(data);
    }

    @Override
    public <A extends Attribute> void overrideSaveMode(Class<A> attributeClass, String saveMode) {
        this.attributeSaveModes.put(attributeClass, saveMode);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute> A loadAttributeFromDataGeneric(AttributeData<?, ?> data, UUID id) {
        return this.loadAttributeFromData((AttributeData<A, ?>) data, id);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute> A loadAttributeFromData(AttributeData<A, ?> data, UUID id) {
        var adapter = data.adapters().get(this.getSaveMode());
        var instance = data.constructor().apply(id);

        if (adapter == null) {
            if (instance instanceof AttributeAdapter) {
                ((AttributeAdapter<A>) instance).load(instance);
            }
        } else {
            adapter.load(instance);
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute> void saveAttributeFromDataGeneric(AttributeData<?, ?> data, A attribute) {
        this.saveAttributeFromData((AttributeData<A, ?>) data, attribute);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Attribute> void saveAttributeFromData(AttributeData<A, ?> data, A attribute) {
        var adapter = this.getAdapter(data);
        if (adapter == null) {
            if (attribute instanceof AttributeAdapter) {
                ((AttributeAdapter<A>) attribute).save(attribute);
            }
        } else {
            adapter.save(attribute);
        }
    }

    protected <A extends Attribute> AttributeAdapter<A> getAdapter(AttributeData<A, ?> data) {
        var saveMode = this.attributeSaveModes.getOrDefault(data.attributeClass(), this.getSaveMode());
        return data.adapters().get(saveMode.toLowerCase(Locale.ROOT));
    }
}
