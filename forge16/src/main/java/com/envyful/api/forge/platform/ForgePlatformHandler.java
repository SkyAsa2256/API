package com.envyful.api.forge.platform;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.platform.PlatformHandler;
import com.envyful.api.text.Placeholder;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collection;

public class ForgePlatformHandler implements PlatformHandler<ICommandSource> {

    private static final ForgePlatformHandler INSTANCE = new ForgePlatformHandler();

    private ForgePlatformHandler() {}

    public static PlatformHandler<?> getInstance() {
        return INSTANCE;
    }

    @Override
    public void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        for (var parsedMessage : UtilChatColour.colour(message, placeholders)) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(parsedMessage, ChatType.SYSTEM, Util.NIL_UUID);
        }
    }

    @Override
    public void sendMessage(ICommandSource player, Collection<String> message, Placeholder... placeholders) {
        for (var parsedMessage : UtilChatColour.colour(message, placeholders)) {
            player.sendMessage(parsedMessage, Util.NIL_UUID);
        }
    }

    @Override
    public void runSync(Runnable runnable) {
        ServerLifecycleHooks.getCurrentServer().execute(runnable);
    }

    @Override
    public void runLater(Runnable runnable, int delayTicks) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickDelayedTask(
                        ServerLifecycleHooks.getCurrentServer().getTickCount() + delayTicks,
                runnable));
    }

    @Override
    public double getTPS() {
        return ServerLifecycleHooks.getCurrentServer().getAverageTickTime();
    }
}
