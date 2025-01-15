package com.envyful.api.config.type;

import com.envyful.api.math.RandomWeightedSet;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Config version of the {@link RandomWeightedSet} class
 *
 * @param <A> The type of the object
 */
@ConfigSerializable
public class ConfigRandomWeightedSet<A> implements Serializable {

    private Map<String, WeightedObject<A>> entries = new HashMap<>();
    private transient RandomWeightedSet<A> weightedSet = null;

    public ConfigRandomWeightedSet(Map<String, WeightedObject<A>> entries) {
        this.entries = entries;
    }

    public ConfigRandomWeightedSet(WeightedObject<A>... entries) {
        this.entries = new HashMap<>();

        for (WeightedObject<A> entry : entries) {
            this.entries.put(String.valueOf(this.entries.size()), entry);
        }
    }

    public ConfigRandomWeightedSet() {
    }

    public void add(WeightedObject<A> entry) {
        this.entries.put(String.valueOf(this.entries.size()), entry);
        this.weightedSet = null;
    }

    public RandomWeightedSet<A> getWeightedSet() {
        if (this.weightedSet == null) {
            this.weightedSet = new RandomWeightedSet<>();

            for (WeightedObject<A> value : this.entries.values()) {
                this.weightedSet.add(value.getObject(), value.weight);
            }
        }

        return this.weightedSet;
    }

    public A getRandom() {
        return this.getWeightedSet().getRandom();
    }

    public static <A> Builder<A> builder(A entry, double weight) {
        return new Builder<A>().entry(entry, weight);
    }

    @ConfigSerializable
    public static class WeightedObject<A> {

        protected double weight;
        protected A object;

        public WeightedObject(double weight, A object) {
            this.weight = weight;
            this.object = object;
        }

        public WeightedObject() {
        }

        public double getWeight() {
            return this.weight;
        }

        public A getObject() {
            return this.object;
        }
    }

    public static class Builder<A> {

        private Map<String, WeightedObject<A>> entries = new HashMap<>();

        protected Builder() {
        }

        public Builder<A> entry(A entry, double weight) {
            this.entries.put(String.valueOf(entries.size() + 1), new WeightedObject<>(weight, entry));
            return this;
        }

        public ConfigRandomWeightedSet<A> build() {
            return new ConfigRandomWeightedSet<>(this.entries);
        }
    }
}
