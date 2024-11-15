package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.attribute.PlayerAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

public abstract class ManagedForgeAttribute<A>
        extends PlayerAttribute<A, ForgeEnvyPlayer, ServerPlayerEntity> {

    protected ManagedForgeAttribute(UUID id, A manager) {
        super(id, manager);
    }
}
