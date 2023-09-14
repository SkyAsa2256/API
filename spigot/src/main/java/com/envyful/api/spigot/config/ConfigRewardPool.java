package com.envyful.api.spigot.config;

import com.envyful.api.config.type.ConfigRandomWeightedSet;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.text.Placeholder;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigRewardPool<T extends ConfigReward> extends AbstractYamlConfig {

    private T guaranteedReward;
    private int rewardRollsMin;
    private int rewardRollsMax;
    private ConfigRandomWeightedSet<T> rewards;

    private ConfigRewardPool(T guaranteedReward, int rewardRollsMin, int rewardRollsMax, ConfigRandomWeightedSet<T> rewards) {
        this.guaranteedReward = guaranteedReward;
        this.rewardRollsMin = rewardRollsMin;
        this.rewardRollsMax = rewardRollsMax;
        this.rewards = rewards;
    }

    public ConfigRewardPool() {
    }

    public List<T> getRandomRewards() {
        List<T> randomlySelectedRewards = Lists.newArrayList();

        for (int i = 0; i < UtilRandom.randomInteger(this.rewardRollsMin, this.rewardRollsMax); i++) {
            randomlySelectedRewards.add(this.rewards.getRandom());
        }

        if (this.guaranteedReward != null) {
            randomlySelectedRewards.add(guaranteedReward);
        }

        return randomlySelectedRewards;
    }

    public void give(Player player, Placeholder... transformers) {
        for (ConfigReward randomReward : this.getRandomRewards()) {
            randomReward.execute(player, transformers);
        }
    }

    public static <A extends ConfigReward> Builder<A> builder(Class<A> ignored) {
        return new Builder<>(ignored);
    }

    public static class Builder<A extends ConfigReward> {

        private A guaranteedReward;
        private int rewardRollsMin;
        private int rewardRollsMax;
        private ConfigRandomWeightedSet<A> rewards;

        private Builder(Class<A> ignored) {}

        public Builder<A> guranteedReward(A reward) {
            this.guaranteedReward = reward;
            return this;
        }

        public Builder<A> minRolls(int minRolls) {
            this.rewardRollsMin = minRolls;
            return this;
        }

        public Builder<A> maxRolls(int maxRolls) {
            this.rewardRollsMax = maxRolls;
            return this;
        }

        public Builder<A> rewards(ConfigRandomWeightedSet<A> rewards) {
            this.rewards = rewards;
            return this;
        }

        public ConfigRewardPool<A> build() {
            return new ConfigRewardPool<>(
                    this.guaranteedReward, this.rewardRollsMin, this.rewardRollsMax,
                    this.rewards
            );
        }
    }
}
