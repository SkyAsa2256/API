package com.envyful.api.spigot.config;

import com.envyful.api.config.type.ConfigRandomWeightedSet;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.text.Placeholder;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigRewardPool {

    private ConfigReward guaranteedReward;
    private int rewardRollsMin;
    private int rewardRollsMax;
    private ConfigRandomWeightedSet<ConfigReward> rewards;

    private ConfigRewardPool(ConfigReward guaranteedReward, int rewardRollsMin, int rewardRollsMax, ConfigRandomWeightedSet<ConfigReward> rewards) {
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

    public void give(Player player, Placeholder... transformers) {
        if (this.guaranteedReward != null) {
            this.guaranteedReward.execute(player, transformers);
        }

        for (ConfigReward randomReward : this.getRandomRewards()) {
            randomReward.execute(player, transformers);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ConfigReward guaranteedReward;
        private int rewardRollsMin;
        private int rewardRollsMax;
        private ConfigRandomWeightedSet<ConfigReward> rewards;

        private Builder() {}

        public Builder guranteedReward(ConfigReward reward) {
            this.guaranteedReward = reward;
            return this;
        }

        public Builder minRolls(int minRolls) {
            this.rewardRollsMin = minRolls;
            return this;
        }

        public Builder maxRolls(int maxRolls) {
            this.rewardRollsMax = maxRolls;
            return this;
        }

        public Builder rewards(ConfigRandomWeightedSet<ConfigReward> rewards) {
            this.rewards = rewards;
            return this;
        }

        public ConfigRewardPool build() {
            return new ConfigRewardPool(
                    this.guaranteedReward, this.rewardRollsMin, this.rewardRollsMax,
                    this.rewards
            );
        }
    }
}
