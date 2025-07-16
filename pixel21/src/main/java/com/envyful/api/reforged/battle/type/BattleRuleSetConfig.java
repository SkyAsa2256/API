package com.envyful.api.reforged.battle.type;

import com.envyful.api.reforged.battle.BattleRulesConfig;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRuleSet;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class BattleRuleSetConfig implements BattleRulesConfig {

    private String ruleSet;

    public BattleRuleSetConfig() {}

    public BattleRuleSetConfig(String ruleSet) {
        this.ruleSet = ruleSet;
    }

    public BattleRuleSetConfig(Holder<BattleRuleSet> holder) {
        this.ruleSet = holder.getKey().location().toString();
    }

    public BattleRuleSetConfig(ResourceKey<BattleRuleSet> ruleSet) {
        this.ruleSet = ruleSet.location().toString();
    }

    @Override
    public BattleRules createRules() {
        var registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(PixelmonRegistry.BATTLE_RULE_SET_REGISTRY);
        var resourceLocation = ResourceLocationHelper.of(this.ruleSet);

        if (resourceLocation == null) {
            throw new IllegalArgumentException("Invalid rule set: " + this.ruleSet);
        }

        var holder = registry.getHolder(resourceLocation).orElseThrow(() -> new IllegalArgumentException("Rule not found: " + this.ruleSet));
        return holder.value().createRules();
    }
}
