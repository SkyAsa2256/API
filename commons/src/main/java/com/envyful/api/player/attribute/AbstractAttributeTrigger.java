package com.envyful.api.player.attribute;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.google.common.collect.Lists;

import java.util.List;

public abstract class AbstractAttributeTrigger<T> implements AttributeTrigger<T> {

    protected List<PlayerManager.AttributeData<?, ?, T>> attributes = Lists.newArrayList();

    @Override
    public void addAttribute(PlayerManager.AttributeData<?, ?, T> attribute) {
        this.attributes.add(attribute);
    }

    protected boolean shouldLoad(EnvyPlayer<T> player, PlayerManager.AttributeData<?, ?, T> attributeData) {
        for (var predicate : attributeData.predicates()) {
            if (!predicate.test(player)) {
                return false;
            }
        }

        return true;
    }
}
