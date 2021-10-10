package com.envyful.api.config.type;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class PermissibleConfigItem extends PositionableConfigItem {

    private String permission;

    public PermissibleConfigItem() {
        super();
    }

    public PermissibleConfigItem(String type, int amount, byte damage, String name, List<String> lore, int xPos,
                                 int yPos, String permission, Map<String, NBTValue> nbt) {
        super(type, amount, damage, name, lore, xPos, yPos, nbt);

        this.permission = permission;
    }

    public String getPermission() {
        return this.permission;
    }
}
