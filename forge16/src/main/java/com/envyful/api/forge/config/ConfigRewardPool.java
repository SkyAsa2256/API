package com.envyful.api.forge.config;

import com.envyful.api.gui.Transformer;
import com.envyful.api.math.RandomWeightedSet;
import com.envyful.api.math.UtilRandom;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigRewardPool {

    private ConfigReward guaranteedReward;
    private int rewardRollsMin;
    private int rewardRollsMax;
    private RandomWeightedSet<ConfigReward> rewards;

    public ConfigRewardPool(ConfigReward guaranteedReward, int rewardRollsMin, int rewardRollsMax, RandomWeightedSet<ConfigReward> rewards) {
        this.guaranteedReward = guaranteedReward;
        this.rewardRollsMin = rewardRollsMin;
        this.rewardRollsMax = rewardRollsMax;
        this.rewards = rewards;
    }

    public ConfigRewardPool() {
    }

    public List<ConfigReward> getRandomRewards() {
        List<ConfigReward> randomlySelectedRewards = Lists.newArrayList();

        for (int i = 0; i < UtilRandom.randomInteger(this.rewardRollsMin, this.rewardRollsMax); i++) {
            randomlySelectedRewards.add(this.rewards.getRandom());
        }

        return randomlySelectedRewards;
    }

    public void give(ServerPlayerEntity player, Transformer... transformers) {
        if (this.guaranteedReward != null) {
            this.guaranteedReward.execute(player, transformers);
        }

        for (ConfigReward randomReward : this.getRandomRewards()) {
            randomReward.execute(player, transformers);
        }
    }
}
