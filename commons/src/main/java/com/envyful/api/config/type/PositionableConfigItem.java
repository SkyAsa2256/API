package com.envyful.api.config.type;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class PositionableConfigItem extends ConfigItem {

    private int xPos = 0;
    private int yPos = 0;

    public PositionableConfigItem() {
        super();
    }

    public PositionableConfigItem(String type, int amount, byte damage, String name, List<String> lore, int xPos,
                                  int yPos, Map<String, NBTValue> nbt) {
        super(type, amount, damage, name, lore, nbt);

        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getXPos() {
        return this.xPos;
    }

    public int getYPos() {
        return this.yPos;
    }
}
