package com.envyful.api.forge.config;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.Transformer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigReward {

    private List<String> commands;
    private List<String> messages;

    public ConfigReward(List<String> commands, List<String> messages) {
        this.commands = commands;
        this.messages = messages;
    }

    public ConfigReward() {
    }

    public void execute(ServerPlayerEntity player, Transformer... transformers) {
        if (this.commands != null && !this.commands.isEmpty()) {
            for (String command : this.commands) {
                for (Transformer transformer : transformers) {
                    command = transformer.transformName(command);
                }

                UtilForgeServer.executeCommand(command.replace("%player%", player.getName().getString()));
            }
        }

        if (this.messages != null && !this.messages.isEmpty()) {
            for (String message : this.messages) {
                for (Transformer transformer : transformers) {
                    message = transformer.transformName(message);
                }

                player.sendMessage(UtilChatColour.colour(message), Util.NIL_UUID);
            }
        }
    }
}
