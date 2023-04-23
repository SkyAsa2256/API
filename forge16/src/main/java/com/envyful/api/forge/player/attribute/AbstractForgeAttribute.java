package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.PlayerAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class AbstractForgeAttribute<A> extends PlayerAttribute<A, ForgeEnvyPlayer, ServerPlayerEntity> {

    protected AbstractForgeAttribute(A manager, PlayerManager<ForgeEnvyPlayer, ServerPlayerEntity> playerManager) {
        super(manager, playerManager);
    }
}
