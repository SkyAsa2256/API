package com.envyful.api.spigot.player.attribute;

import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.PlayerAttribute;
import com.envyful.api.spigot.player.SpigotEnvyPlayer;
import org.bukkit.entity.Player;

public abstract class AbstractSpigotAttribute<A> extends PlayerAttribute<A, SpigotEnvyPlayer, Player> {

    protected AbstractSpigotAttribute(A manager, PlayerManager<SpigotEnvyPlayer, Player> playerManager) {
        super(manager, playerManager);
    }
}
