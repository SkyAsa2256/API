package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.attribute.PlayerAttribute;

/**
 *
 * Abstract implementation of the {@link PlayerAttribute} for the forge platform. This handles storing the manager
 * and the forge implementation of the "parent" {@link ForgeEnvyPlayer} class.
 *
 * @param <A> The manager class parameter
 */
public abstract class AbstractForgeAttribute<A> implements PlayerAttribute<A> {

    protected final A manager;
    protected final ForgeEnvyPlayer parent;

    protected AbstractForgeAttribute(A manager, ForgeEnvyPlayer parent) {
        this.manager = manager;
        this.parent = parent;
    }
}
