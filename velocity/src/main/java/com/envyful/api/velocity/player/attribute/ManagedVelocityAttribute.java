package com.envyful.api.velocity.player.attribute;

import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.velocity.player.VelocityEnvyPlayer;
import com.velocitypowered.api.proxy.Player;

/**
 *
 * An abstract class for a managed attribute for a {@link VelocityEnvyPlayer}
 *
 * @param <A> The type of the attribute
 */
public abstract class ManagedVelocityAttribute<A>
        extends PlayerAttribute<A, VelocityEnvyPlayer, Player> {

    protected ManagedVelocityAttribute(
            A manager
    ) {
        super(manager);
    }
}
