package com.envyful.api.player;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.player.save.SaveManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *
 * This interface is designed to provide basic useful
 * methods for all the different player implementations independent
 * of the platform details (i.e. auto-translates all text sent
 * to the player, and makes it less complicated to do
 * different functions such as sending titles etc.).
 * <br>
 * It also stores {@link PlayerAttribute} from the plugin implementation
 * that will include specific data from the
 * plugin / mod. The attributes stored by the plugin's / manager's
 * class as to allow each mod / plugin to have multiple
 * attributes for storing different sets of data.
 *
 * @param <T> The specific platform implementation of the player object.
 */
public abstract class AbstractEnvyPlayer<T> implements EnvyPlayer<T> {

    protected final Map<Class<?>, AttributeInstance<?, ?, T>> attributes = Maps.newHashMap();

    protected final SaveManager<T> saveManager;

    protected T parent;

    protected AbstractEnvyPlayer(SaveManager<T> saveManager) {
        this.saveManager = saveManager;
    }

    @Override
    public T getParent() {
        return this.parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute<B, T>, B> CompletableFuture<A> getAttribute(Class<A> attributeClass) {
        if (!this.attributes.containsKey(attributeClass)) {
            return null;
        }

        AttributeInstance<A, B, T> instance = (AttributeInstance<A, B, T>) this.attributes.get(attributeClass);
        return instance.getAttribute();
    }

    @Override
    public <A extends Attribute<B, T>, B> A getAttributeNow(Class<A> attributeClass) {
        if (!this.attributes.containsKey(attributeClass)) {
            return null;
        }

        var instance = (AttributeInstance<A, B, T>) this.attributes.get(attributeClass);

        if (instance.attribute != null) {
            return instance.attribute;
        }

        if (instance.loadingAttribute == null) {
            return null;
        }

        return instance.loadingAttribute.join();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute<B, T>, B, C extends EnvyPlayer<T>> void setAttribute(A attribute) {
        if (attribute == null) {
            return;
        }

        if (attribute instanceof PlayerAttribute) {
            ((PlayerAttribute<?, C, T>) attribute).setParent((C) this);
        }

        this.attributes.put(attribute.getClass(), new AttributeInstance<>(attribute));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Attribute<B, T>, B, C extends EnvyPlayer<T>> void setAttribute(Class<A> attributeClass, CompletableFuture<A> attribute) {
        attribute.whenComplete((a, throwable) -> {
            if (a instanceof PlayerAttribute) {
                ((PlayerAttribute<?, C, T>) a).setParent((C) this);
            }
        });
        this.attributes.put(attributeClass, new AttributeInstance<>(attribute));
    }

    @Override
    public <A extends Attribute<B, T>, B> void removeAttribute(Class<A> attributeClass) {
        this.attributes.remove(attributeClass);
    }

    @Override
    public List<Attribute<?, T>> getAttributes() {
        List<Attribute<?, T>> attributes = Lists.newArrayList();

        for (var attribute : this.attributes.values()) {
            if (attribute.getAttributeNow() != null) {
                attributes.add(attribute.getAttributeNow());
            }
        }

        return attributes;
    }

    protected static class AttributeInstance<A extends Attribute<B, C>, B, C> {

        private A attribute;
        private CompletableFuture<A> loadingAttribute;

        AttributeInstance(A attribute) {
            this.attribute = attribute;
            this.loadingAttribute = null;
        }

        AttributeInstance(CompletableFuture<A> loadingAttribute) {
            this.attribute = null;
            this.loadingAttribute = loadingAttribute.whenComplete((a, throwable) -> {
                if (throwable != null) {
                    UtilLogger.logger().ifPresent(logger -> logger.error("Failed to load attribute", throwable));
                } else {
                    this.attribute = a;
                    this.loadingAttribute = null;
                }
            });
        }

        CompletableFuture<A> getAttribute() {
            return this.loadingAttribute == null ? CompletableFuture.completedFuture(this.attribute) : this.loadingAttribute;
        }

        @Nullable
        A getAttributeNow() {
            return this.attribute;
        }
    }
}
