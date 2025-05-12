package com.envyful.api.neoforge.command.completion.player;

import com.envyful.api.command.injector.TabCompleter;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class PlayerTabCompleter implements TabCompleter<CommandSource> {

    @Override
    public List<String> getCompletions(CommandSource sender, String[] currentData, Annotation... completionData) {
        List<String> playerNames = new ArrayList<>();

        for (var player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (completionData.length < 1 || (completionData[0] instanceof ExcludeSelfCompletion && sender instanceof Player senderPlayer && player.getName().equals(senderPlayer.getName()))) {
                continue;
            }

            playerNames.add(player.getName().getString());
        }

        return playerNames;
    }
}
