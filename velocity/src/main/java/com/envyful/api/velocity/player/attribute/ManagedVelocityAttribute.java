package com.envyful.api.velocity.player.attribute;

import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.velocity.player.VelocityEnvyPlayer;
import com.envyful.api.velocity.player.VelocityPlayerManager;
import com.velocitypowered.api.proxy.Player;

public abstract class ManagedVelocityAttribute<A>
        extends PlayerAttribute<A, VelocityEnvyPlayer, Player, VelocityPlayerManager> {

    protected ManagedVelocityAttribute(
            A manager, VelocityPlayerManager playerManager
    ) {
        super(manager, playerManager);
    }
}
