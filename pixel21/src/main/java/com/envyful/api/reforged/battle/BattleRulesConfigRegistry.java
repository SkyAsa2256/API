package com.envyful.api.reforged.battle;


import com.envyful.api.config.ConfigTypeSerializer;
import com.envyful.api.reforged.battle.type.BattleRuleListConfig;
import com.envyful.api.reforged.battle.type.BattleRuleSetConfig;
import com.envyful.api.registry.Registry;
import com.envyful.api.registry.config.KeySerializer;

public class BattleRulesConfigRegistry {

    public static final Registry<String, Class<BattleRulesConfig>> REGISTRY = Registry.classBased(KeySerializer.identity());

    public static void init() {
        register("rule_list", BattleRuleListConfig.class);
        register("rule_set", BattleRuleSetConfig.class);

        ConfigTypeSerializer.register(REGISTRY.getTypeSerializer(), BattleRulesConfig.class);
    }

    public static void register(String id, Class<? extends BattleRulesConfig> configClass) {
        REGISTRY.register(id, (Class<BattleRulesConfig>) configClass);
    }
}
