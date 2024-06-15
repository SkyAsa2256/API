package com.envyful.api.reforged.battle;

import com.pixelmonmod.pixelmon.battles.api.rules.BattleRuleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * Config object for a battle rule
 *
 */
@ConfigSerializable
public class ConfigBattleRule {

    private String battleRuleType;
    private String battleRuleValue;

    public ConfigBattleRule(String battleRuleType, String battleRuleValue) {
        this.battleRuleType = battleRuleType;
        this.battleRuleValue = battleRuleValue;
    }

    public ConfigBattleRule() {
    }

    public String getBattleRuleType() {
        return this.battleRuleType;
    }

    public String getBattleRuleValue() {
        return this.battleRuleValue;
    }

    public BattleRules with(BattleRules battleRules) {
        return battleRules.set(BattleRuleRegistry.getProperty(this.battleRuleType), this.battleRuleValue);
    }
}
