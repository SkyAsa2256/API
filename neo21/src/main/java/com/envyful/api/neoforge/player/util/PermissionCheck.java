package com.envyful.api.neoforge.player.util;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 *
 * Simple utility class for checking if a player has a permission node
 * and if not sending them the given message
 *
 */
@ConfigSerializable
public class PermissionCheck {

    protected boolean enabled;
    protected String permission;
    protected List<String> noPermissionMessage;

    public PermissionCheck() {
    }

    protected PermissionCheck(boolean enabled, String permission, List<String> noPermissionMessage) {
        this.enabled = enabled;
        this.permission = permission;
        this.noPermissionMessage = noPermissionMessage;
    }

    public boolean test(ForgeEnvyPlayer player) {
        return this.test(player.getParent());
    }

    public boolean test(ServerPlayer player) {
        if (!this.enabled || UtilPlayer.hasPermission(player, this.permission)) {
            return true;
        }

        PlatformProxy.sendMessage(player, this.noPermissionMessage);
        return false;
    }

    public static PermissionCheck of(String permission, String... failureMessage) {
        return new PermissionCheck(true, permission, List.of(failureMessage));
    }
}
