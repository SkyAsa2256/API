package com.envyful.api.velocity.player.command.completion.player;

import com.envyful.api.command.injector.TabCompleter;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerTabCompleter implements TabCompleter<CommandSource> {

    private ProxyServer proxy;

    public PlayerTabCompleter(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public List<String> getCompletions(
            CommandSource sender, String[] currentData, Annotation... completionData) {
        Collection<Player> allPlayers = this.proxy.getAllPlayers();
        List<String> playerNames = new ArrayList<>(allPlayers.size());

        for (Player player : allPlayers) {
            if (completionData.length < 1 ||
                   ((completionData[0] instanceof ExcludeSelfCompletion && sender instanceof Player)
                            && (player.getUsername().equals(((Player) sender).getUsername())))) {
                continue;
            }

            playerNames.add(player.getUsername());
        }

        return playerNames;
    }
}
