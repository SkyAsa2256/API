package com.envyful.api.config.type;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class PositionableConfigItem extends ConfigItem {

    private int xPos = 0;
    private int yPos = 0;

    public PositionableConfigItem() {
        super();
    }

    public PositionableConfigItem(String type, int amount, byte damage, String name, List<String> lore, int xPos,
                                  int yPos) {
        super(type, amount, damage, name, lore);

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
