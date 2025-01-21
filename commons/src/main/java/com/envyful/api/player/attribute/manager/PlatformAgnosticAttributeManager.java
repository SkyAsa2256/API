package com.envyful.api.player.attribute.manager;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.Attribute;
import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.adapter.AttributeAdapter;
import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.player.attribute.trigger.ClearAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SaveAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SetAttributeTrigger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * Platform agnostic implementation of the {@link AttributeManager} interface
 *
 * @param <A> The attribute holder type
 */
public abstract class PlatformAgnosticAttributeManager<A extends AttributeHolder> implements AttributeManager<A> {

    protected final Map<Class<? extends Attribute>, AttributeData<?, A>> attributeData = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute>, Map<UUID, Attribute>> sharedAttributes = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Attribute>, String> attributeSaveModes = new ConcurrentHashMap<>();

    protected final Map<Class<?>, AttributeTrigger<A>> triggersByEvent = new HashMap<>();
    protected final Map<Class<?>, Supplier<AttributeTrigger<A>>> registeredTriggers = new HashMap<>();

    protected String globalSaveMode;
    protected BiConsumer<UUID, Throwable> errorHandler = (a, throwable) -> UtilLogger.logger().ifPresent(logger -> logger.error("Failed to load attribute for id " + a.toString(), throwable));

    protected PlatformAgnosticAttributeManager() {
        this.registerAttributeTrigger(SetAttributeTrigger.class, SetAttributeTrigger::new);
        this.registerAttributeTrigger(ClearAttributeTrigger.class, ClearAttributeTrigger::new);
        this.registerAttributeTrigger(SaveAttributeTrigger.class, SaveAttributeTrigger::new);
    }

    protected PlatformAgnosticAttributeManager(BiConsumer<UUID, Throwable> errorHandler) {
        this();

        this.errorHandler = errorHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> CompletableFuture<X> loadAttribute(Class<? extends X> attributeClass, UUID id) {
        return CompletableFuture.supplyAsync(() -> {
                    AttributeData<X, A> data = (AttributeData<X, A>) this.attributeData.get(attributeClass);

                    if (data.shared()) {
                        X sharedAttribute = (X) this.sharedAttributes.getOrDefault(attributeClass, Map.of()).get(id);

                        if (sharedAttribute == null) {
                            sharedAttribute = this.loadAttributeFromData(data, id);
                            this.sharedAttributes.computeIfAbsent(attributeClass, ___ -> new ConcurrentHashMap<>()).put(id, sharedAttribute);
                        }

                        return sharedAttribute;
                    } else {
                        return this.loadAttributeFromData(data, id);
                    }
                }, UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE)
                .exceptionally(throwable -> {
                    this.errorHandler.accept(id, throwable);
                    return null;
                });
    }

    @SuppressWarnings("unchecked")
    protected <X extends Attribute> X loadAttributeFromData(AttributeData<X, A> data, UUID id) {
        var adapter = this.getAdapter(data);
        var instance = data.constructor().apply(id);

        if (adapter == null) {
            if (instance instanceof AttributeAdapter) {
                ((AttributeAdapter<X>) instance).load(instance);
            }
        } else {
            adapter.load(instance);
        }

        return instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends Attribute> CompletableFuture<Void> saveAttribute(X attribute) {
        if (!this.attributeData.containsKey(attribute.getClass())) {
            if (attribute instanceof AttributeAdapter) {
                return ((AttributeAdapter<X>) attribute).save(attribute);
            }

            return CompletableFuture.completedFuture(null);
        } else {
            AttributeAdapter<X> adapter = (AttributeAdapter<X>) this.getAdapter(this.attributeData.get(attribute.getClass()));

            if (adapter == null) {
                if (attribute instanceof AttributeAdapter) {
                    return ((AttributeAdapter<X>) attribute).save(attribute);
                }

                return CompletableFuture.completedFuture(null);
            }

            return adapter.save(attribute);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Attribute> AttributeAdapter<T> getAdapter(Class<T> attributeClass) {
        return (AttributeAdapter<T>) this.getAdapter(this.attributeData.get(attributeClass));
    }

    protected <X extends Attribute> AttributeAdapter<X> getAdapter(AttributeData<X, A> data) {
        return data.adapters().get(this.getSaveMode(data.attributeClass()).toLowerCase(Locale.ROOT));
    }

    @Override
    public <T extends Attribute> UUID mapId(Class<T> attributeClass, UUID uuid) {
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
    }

    @Override
    public void setGlobalSaveMode(String globalSaveMode) {
        this.globalSaveMode = globalSaveMode;
    }

    @Override
    public void overrideSaveMode(Class<? extends Attribute> attributeClass, String saveMode) {
        this.attributeSaveModes.put(attributeClass, saveMode);
    }

    @Override
    public String getSaveMode(Class<? extends Attribute> attributeClass) {
        return this.attributeSaveModes.getOrDefault(attributeClass, this.globalSaveMode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> AttributeTrigger<A> getAttributeTriggerInstance(Class<Y> eventClass, Class<? extends AttributeTrigger<A>> triggerClass, Function<Y, List<A>> converter) {
        var trigger = triggersByEvent.computeIfAbsent(eventClass, aClass -> {
            var supplier = this.registeredTriggers.get(triggerClass);

            if (supplier == null) {
                throw new IllegalArgumentException("No trigger registered for class: " + triggerClass.getName());
            }

            var instance = supplier.get();

            this.registerListeners(eventClass, converter, instance);

            return instance;
        });

        return trigger;
    }

    protected abstract <Y> void registerListeners(Class<Y> eventClass, Function<Y, List<A>> converter, AttributeTrigger<A> trigger);

    @Override
    @SuppressWarnings("unchecked")
    public <Y extends AttributeTrigger<A>> void registerAttributeTrigger(Class<Y> triggerClass, Supplier<Y> constructor) {
        this.registeredTriggers.put(triggerClass, (Supplier<AttributeTrigger<A>>) constructor);
    }
}
