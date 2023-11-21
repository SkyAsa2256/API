package com.envyful.api.forge.command.completion.player;

import com.envyful.api.command.injector.TabCompleter;
import com.google.common.collect.Lists;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.annotation.Annotation;
import java.util.List;

public class PlayerTabCompleter implements TabCompleter<CommandSource> {

    @Override
    public List<String> getCompletions(CommandSource sender, String[] currentData, Annotation... completionData) {
        List<String> playerNames = Lists.newArrayList();

        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (completionData.length < 1 || (completionData[0] instanceof ExcludeSelfCompletion && sender instanceof Player senderPlayer && player.getName().equals(senderPlayer.getName()))) {
                continue;
            }

            playerNames.add(player.getName().getString());
        }

        return playerNames;
    }
}
