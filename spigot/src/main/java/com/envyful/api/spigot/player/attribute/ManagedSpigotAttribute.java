package com.envyful.api.spigot.player.attribute;

import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class ManagedSpigotAttribute<A>
        extends PlayerAttribute<A, SpigotEnvyPlayer, Player> {

    protected ManagedSpigotAttribute(UUID id, A manager) {
        super(id, manager);
    }
}
