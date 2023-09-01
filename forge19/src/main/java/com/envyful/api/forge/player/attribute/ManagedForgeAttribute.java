package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.player.attribute.PlayerAttribute;
import net.minecraft.server.level.ServerPlayer;

public abstract class ManagedForgeAttribute<A>
        extends PlayerAttribute<A, ForgeEnvyPlayer, ServerPlayer, ForgePlayerManager> {

    protected ManagedForgeAttribute(
            A manager, ForgePlayerManager playerManager
    ) {
        super(manager, playerManager);
    }
}
