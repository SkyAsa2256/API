package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.PlayerAttribute;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractForgeAttribute<A> extends PlayerAttribute<A, ForgeEnvyPlayer, ServerPlayer> {

    protected AbstractForgeAttribute(A manager, PlayerManager<ForgeEnvyPlayer, ServerPlayer> playerManager) {
        super(manager, playerManager);
    }
}
