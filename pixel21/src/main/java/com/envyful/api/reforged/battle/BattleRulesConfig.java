package com.envyful.api.reforged.battle;

import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public interface BattleRulesConfig {

    BattleRules createRules();

}
