package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.player.attribute.PlayerAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class ManagedForgeAttribute<A>
        extends PlayerAttribute<A, ForgeEnvyPlayer, ServerPlayerEntity, ForgePlayerManager> {

    protected ManagedForgeAttribute(
            A manager, ForgePlayerManager playerManager
    ) {
        super(manager, playerManager);
    }
}
