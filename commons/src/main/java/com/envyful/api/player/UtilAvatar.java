package com.envyful.api.player;

import java.util.UUID;

/**
 *
 * A utility class for getting the avatar URL of a player
 *
 **/
public class UtilAvatar {

    /**
     *
     * Gets the avatar URL of the player
     *
     * @param player The player to get the avatar URL of
     * @return The URL of the player's avatar
     */
    public static String getAvatarUrl(EnvyPlayer<?> player) {
        return "https://crafatar.com/avatars/{uuid-nodashes}.png?size=128".replace("{uuid-nodashes}", player.getUniqueId().toString().replace("-", ""));
    }

    /**
     *
     * Gets the avatar URL of the player
     *
     * @param player The uuid of the player to get the avatar URL of
     * @return The URL of the player's avatar
     */
    public static String getAvatarUrl(UUID player) {
        return "https://crafatar.com/avatars/{uuid-nodashes}.png?size=128".replace("{uuid-nodashes}", player.toString().replace("-", ""));
    }
}
