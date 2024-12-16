package com.envyful.api.player;

import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.player.attribute.holder.PlatformAgnosticAttributeHolder;

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
public abstract class AbstractEnvyPlayer<T> extends PlatformAgnosticAttributeHolder implements EnvyPlayer<T> {

    protected T parent;

    protected AbstractEnvyPlayer() {}

    @Override
    public T getParent() {
        return this.parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    public <A extends Attribute<B>, B> void setAttribute(A attribute) {
        if (attribute == null) {
            return;
        }

        if (attribute instanceof PlayerAttribute) {
            this.attemptSetParent(attribute);
        }

        super.setAttribute(attribute);
    }

    @Override
    public <A extends Attribute<B>, B> void setAttribute(Class<A> attributeClass, CompletableFuture<A> attribute) {
        attribute.whenComplete((a, throwable) -> {
            if (a instanceof PlayerAttribute) {
                this.attemptSetParent(a);
            }
        });

        super.setAttribute(attributeClass, attribute);
    }

    @SuppressWarnings("unchecked")
    private <A extends Attribute<B>, B, C extends EnvyPlayer<T>> void attemptSetParent(A attribute) {
        ((PlayerAttribute<?, C, T>) attribute).setParent((C) this);
    }
}
