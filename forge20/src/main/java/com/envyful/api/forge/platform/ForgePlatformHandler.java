package com.envyful.api.forge.platform;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.platform.PlatformHandler;
import com.envyful.api.text.Placeholder;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.TickTask;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collection;

public class ForgePlatformHandler implements PlatformHandler<CommandSource> {

    private static final ForgePlatformHandler INSTANCE = new ForgePlatformHandler();

    private ForgePlatformHandler() {}

    public static PlatformHandler<?> getInstance() {
        return INSTANCE;
    }

    @Override
    public void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        for (var parsedMessage : UtilChatColour.colour(message, placeholders)) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(parsedMessage, false);
        }
    }

    @Override
    public void sendMessage(CommandSource player, Collection<String> message, Placeholder... placeholders) {
        for (var parsedMessage : UtilChatColour.colour(message, placeholders)) {
            player.sendSystemMessage(parsedMessage);
        }
    }

    @Override
    public void runSync(Runnable runnable) {
        ServerLifecycleHooks.getCurrentServer().execute(runnable);
    }

    @Override
    public void runLater(Runnable runnable, int delayTicks) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(
                        ServerLifecycleHooks.getCurrentServer().getTickCount() + delayTicks,
                runnable));
    }

    @Override
    public double getTPS() {
        return ServerLifecycleHooks.getCurrentServer().getAverageTickTime();
    }
}
