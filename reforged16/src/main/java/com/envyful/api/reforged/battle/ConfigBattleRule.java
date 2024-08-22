package com.envyful.api.reforged.battle;

import com.envyful.api.concurrency.UtilLogger;
import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRuleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.api.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.value.ClausesValue;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Set;

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
        var property = BattleRuleRegistry.getProperty(this.battleRuleType);
        Set<BattleClause> clauses = Sets.newHashSet(battleRules.getClauseList());

        if (property != null) {
            battleRules.set(property, this.battleRuleValue);
        } else {
            var clause = BattleClauseRegistry.getClause(this.battleRuleType);
            if (clause != null) {
                clauses.add(clause);
            } else {
                UtilLogger.logger().ifPresent(logger -> logger.error("Invalid battle rule or clause found `{}`", this.battleRuleType));
            }
        }

        battleRules.set(BattleRuleRegistry.CLAUSES, new ClausesValue(clauses));

        return battleRules.set(BattleRuleRegistry.getProperty(this.battleRuleType), this.battleRuleValue);
    }
}
