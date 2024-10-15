package com.envyful.api.forge.player.attribute;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.attribute.PlayerAttribute;
import net.minecraft.server.level.ServerPlayer;

/**
 *
 * Abstract class for Forge attributes
 *
 * @param <A> The type of the attribute
 */
public abstract class ManagedForgeAttribute<A>
        extends PlayerAttribute<A, ForgeEnvyPlayer, ServerPlayer> {

    protected ManagedForgeAttribute(
            A manager
    ) {
        super(manager);
    }
}
