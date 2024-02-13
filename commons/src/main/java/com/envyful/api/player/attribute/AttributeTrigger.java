package com.envyful.api.player.attribute;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;

public interface AttributeTrigger<T> {

    void addAttribute(PlayerManager.AttributeData<?, ?, T> attribute);

    void trigger(EnvyPlayer<T> player);

}
