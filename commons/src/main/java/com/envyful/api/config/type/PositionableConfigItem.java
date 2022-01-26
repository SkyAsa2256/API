package com.envyful.api.config.type;

import com.envyful.api.type.Pair;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class PositionableConfigItem extends ConfigItem {

    private Map<String, Pair<Integer, Integer>> positions = Maps.newHashMap();

    public PositionableConfigItem() {
        super();
    }

    public PositionableConfigItem(String type, int amount, byte damage, String name, List<String> lore, int xPos,
                                  int yPos, Map<String, NBTValue> nbt) {
        super(type, amount, damage, name, lore, nbt);

        this.positions.put("first", Pair.of(xPos, yPos));
    }

    public List<Pair<Integer, Integer>> getPositions() {
        return Lists.newArrayList(this.positions.values());
    }
}
