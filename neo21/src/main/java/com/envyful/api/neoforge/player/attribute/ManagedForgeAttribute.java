package com.envyful.api.neoforge.player.attribute;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.player.attribute.PlayerAttribute;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 *
 * Abstract class for Forge attributes
 *
 * @param <A> The type of the attribute
 */
public abstract class ManagedForgeAttribute<A>
        extends PlayerAttribute<A, ForgeEnvyPlayer, ServerPlayer> {

    protected ManagedForgeAttribute(UUID id, A manager) {
        super(id, manager);
    }
}
