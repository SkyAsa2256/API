package com.envyful.api.velocity.player.command.completion.player;

import com.envyful.api.command.injector.TabCompleter;
import com.google.common.collect.Lists;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public class PlayerTabCompleter implements TabCompleter<String, Player> {

    private ProxyServer proxy;

    public PlayerTabCompleter(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public Class<Player> getSenderClass() {
        return Player.class;
    }

    @Override
    public Class<String> getCompletedClass() {
        return String.class;
    }

    @Override
    public List<String> getCompletions(
            Player sender, String[] currentData, Annotation... completionData) {
        Collection<Player> allPlayers = this.proxy.getAllPlayers();
        List<String> playerNames = Lists.newArrayListWithCapacity(allPlayers.size());

        for (Player player : allPlayers) {
            if (completionData.length < 1 ||
                    completionData[0] instanceof ExcludeSelfCompletion) {
                if (player.getUsername().equals(sender.getUsername())) {
                    continue;
                }
            }

            playerNames.add(player.getUsername());
        }

        return playerNames;
    }
}
