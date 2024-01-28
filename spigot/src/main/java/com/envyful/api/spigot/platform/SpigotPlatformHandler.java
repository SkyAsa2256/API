package com.envyful.api.spigot.platform;

import com.envyful.api.platform.PlatformHandler;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class SpigotPlatformHandler implements PlatformHandler<Audience> {

    protected final Plugin plugin;

    private SpigotPlatformHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public static PlatformHandler<Audience> of(Plugin plugin) {
        return new SpigotPlatformHandler(plugin);
    }

    @Override
    public boolean hasPermission(Audience player, String permission) {
        return !(player instanceof Player) || ((Player) player).hasPermission(permission);
    }

    @Override
    public void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        for (String s : message) {
            for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(s, placeholders)) {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(handlePlaceholder));
            }
        }
    }

    @Override
    public void sendMessage(Audience player, Collection<String> message, Placeholder... placeholders) {
        for (String s : message) {
            for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(s, placeholders)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(handlePlaceholder));
            }
        }
    }

    @Override
    public void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    @Override
    public void runLater(Runnable runnable, int delayTicks) {
        Bukkit.getScheduler().runTaskLater(this.plugin, runnable, delayTicks);
    }

    @Override
    public double getTPS() {
        return Bukkit.getServer().getTPS()[0];
    }
}
