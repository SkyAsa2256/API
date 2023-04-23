package com.envyful.api.velocity.player.attribute;

import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.velocity.player.VelocityEnvyPlayer;
import com.velocitypowered.api.proxy.Player;

public abstract class AbstractVelocityAttribute<A>
        extends PlayerAttribute<A, VelocityEnvyPlayer, Player> {

    protected AbstractVelocityAttribute(
            A manager, PlayerManager<VelocityEnvyPlayer, Player> playerManager
    ) {
        super(manager, playerManager);
    }
}
