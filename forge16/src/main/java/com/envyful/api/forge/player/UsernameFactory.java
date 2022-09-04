package com.envyful.api.forge.player;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.database.impl.redis.Subscribe;
import com.envyful.api.json.JsonUsernameCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.UUID;

public class UsernameFactory {

    private static JsonUsernameCache usernameCache;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Listener());
    }

    public static JsonUsernameCache getUsernameCache() {
        return usernameCache;
    }

    public static void setUsernameCache(JsonUsernameCache usernameCache) {
        UsernameFactory.usernameCache = usernameCache;
    }

    public static void addCache(UUID uuid, String name) {
        usernameCache.addCache(uuid, name);
    }

    public static UUID getUUID(String name) {
        return usernameCache.getUUID(name);
    }

    public static void invalidateUUID(UUID uuid) {
        usernameCache.invalidateUUID(uuid);
    }

    public static void setSaving(boolean saving) {
        usernameCache.setSaving(saving);
    }

    public static void save() {
        usernameCache.save();
    }

    public static class Listener {

        @SubscribeEvent
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            UsernameFactory.invalidateUUID(event.getPlayer().getUUID());
            UsernameFactory.addCache(event.getPlayer().getUUID(), event.getPlayer().getName().getString());
        }

        @SubscribeEvent
        public void onServerShuttingDown(FMLServerStoppingEvent event) {
            UtilConcurrency.runAsync(UsernameFactory::save);
        }
    }
}
