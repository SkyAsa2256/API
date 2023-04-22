package com.envyful.api.player.save.impl;

import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.save.AbstractSaveManager;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmptySaveManager<T> extends AbstractSaveManager<T> {

    public EmptySaveManager(PlayerManager<?, ?> playerManager) {
        super(playerManager);
    }

    @Override
    public List<Attribute<?, ?>> loadData(UUID uuid) {
        if (this.registeredAttributes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Attribute<?, ?>> attributes = Lists.newArrayList();

        for (Map.Entry<Class<? extends Attribute<?, ?>>, AttributeData> entry : this.registeredAttributes.entrySet()) {
            AttributeData value = entry.getValue();
            Attribute<?, ?> attribute = value.getConstructor().apply(uuid);

            attribute.getId(uuid).whenComplete((o, throwable) -> attribute.loadWithGenericId(o));
            attributes.add(attribute);
        }

        return attributes;
    }

    @Override
    public void saveData(UUID uuid, Attribute<?, ?> attribute) {
        attribute.getId(uuid).whenComplete((o, throwable) -> attribute.saveWithGenericId(o));
    }
}
