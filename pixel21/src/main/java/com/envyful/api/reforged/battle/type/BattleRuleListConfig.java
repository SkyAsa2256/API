package com.envyful.api.reforged.battle.type;

import com.envyful.api.reforged.battle.BattleRulesConfig;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRule;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class BattleRuleListConfig implements BattleRulesConfig {

    private List<String> rules;

    public BattleRuleListConfig() {}

    public BattleRuleListConfig(List<String> rules) {
        this.rules = rules;
    }

    public BattleRuleListConfig(String... rules) {
        this.rules = List.of(rules);
    }

    @Override
    public BattleRules createRules() {
        List<BattleRule> rules = new ArrayList<>();
        var registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(PixelmonRegistry.BATTLE_RULE_REGISTRY);

        for (var rule : this.rules) {
            var resourceLocation = ResourceLocationHelper.of(rule);

            if (resourceLocation == null) {
                throw new IllegalArgumentException("Invalid rule: " + rule);
            }

            var holder = registry.getHolder(resourceLocation).orElseThrow(() -> new IllegalArgumentException("Rule not found: " + rule));
            rules.add(holder.value());
        }

        return new BattleRules(rules);
    }
}
