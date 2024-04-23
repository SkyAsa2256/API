package com.envyful.api.forge.config;

import com.envyful.api.config.type.ConfigRandomWeightedSet;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.math.UtilRandom;
import com.envyful.api.text.Placeholder;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ConfigRewardPool<T extends ConfigReward> extends AbstractYamlConfig {

    private T guaranteedReward;
    private int rewardRollsMin;
    private int rewardRollsMax;
    private double chanceOfAdditionalRewards = 1.0;
    private ConfigRandomWeightedSet<T> rewards;

    protected ConfigRewardPool(Builder<T> builder) {
        this.guaranteedReward = builder.guaranteedReward;
        this.rewardRollsMin = builder.rewardRollsMin;
        this.rewardRollsMax = builder.rewardRollsMax;
        this.rewards = builder.rewards;
        this.chanceOfAdditionalRewards = builder.chanceOfAdditionalRewards;
    }

    public ConfigRewardPool() {
    }

    public List<T> getRandomRewards() {
        List<T> randomlySelectedRewards = Lists.newArrayList();

        if (UtilRandom.chance(this.chanceOfAdditionalRewards)) {
            for (int i = 0; i < UtilRandom.randomInteger(this.rewardRollsMin, this.rewardRollsMax); i++) {
                randomlySelectedRewards.add(this.rewards.getRandom());
            }
        }

        if (this.guaranteedReward != null) {
            randomlySelectedRewards.add(guaranteedReward);
        }

        return randomlySelectedRewards;
    }

    public void give(ServerPlayerEntity player, Placeholder... transformers) {
        for (ConfigReward randomReward : this.getRandomRewards()) {
            randomReward.execute(player, transformers);
        }
    }

    public ConfigRandomWeightedSet<T> getRewards() {
        return this.rewards;
    }

    public static <A extends ConfigReward> Builder<A> builder(A guaranteedReward) {
        return new Builder<A>().guaranteedReward(guaranteedReward);
    }

    public static class Builder<A extends ConfigReward> {

        private A guaranteedReward;
        private int rewardRollsMin;
        private int rewardRollsMax;
        private double chanceOfAdditionalRewards = 1.0;
        private ConfigRandomWeightedSet<A> rewards;

        protected Builder() {}

        public Builder<A> guaranteedReward(A reward) {
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

        public Builder<A> chanceOfAdditionalRewards(double chanceOfAdditionalRewards) {
            this.chanceOfAdditionalRewards = chanceOfAdditionalRewards;
            return this;
        }

        public ConfigRewardPool<A> build() {
            return new ConfigRewardPool<>(this);
        }
    }
}
