package com.envyful.api.spigot.player.attribute;

import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import org.bukkit.entity.Player;

public abstract class ManagedSpigotAttribute<A>
        extends PlayerAttribute<A, SpigotEnvyPlayer, Player> {

    protected ManagedSpigotAttribute(
            A manager
    ) {
        super(manager);
    }
}
