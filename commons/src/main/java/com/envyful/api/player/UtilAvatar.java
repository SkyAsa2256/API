package com.envyful.api.player;

import java.util.UUID;

/**
 *
 * A utility class for getting the avatar URL of a player
 *
 **/
public class UtilAvatar {

    public static String getAvatarUrl(EnvyPlayer<?> player) {
        return "https://crafatar.com/avatars/{uuid-nodashes}.png?size=128".replace("{uuid-nodashes}", player.getUniqueId().toString().replace("-", ""));
    }
    public static String getAvatarUrl(UUID player) {
        return "https://crafatar.com/avatars/{uuid-nodashes}.png?size=128".replace("{uuid-nodashes}", player.toString().replace("-", ""));
    }
}
