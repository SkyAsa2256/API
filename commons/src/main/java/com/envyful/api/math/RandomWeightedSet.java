package com.envyful.api.math;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * A set of items that have weighted and can be randomly selected from according to said weightings
 *
 * @param <A> The type
 */
public class RandomWeightedSet<A> extends HashMap<A, Double> {

    protected final transient NavigableMap<Double, A> treeMap = new TreeMap<>();
    protected transient double totalWeight = 0;

    public RandomWeightedSet() {
        super();
    }

    public RandomWeightedSet(A a, double weight) {
        this();

        this.add(a, weight);
    }

    public RandomWeightedSet<A> add(A a, double weight) {
        super.put(a, weight);
        this.totalWeight += weight;
        this.treeMap.put(this.totalWeight, a);
        return this;
    }

    @Override
    public void clear() {
        super.clear();

        this.totalWeight = 0;
        this.treeMap.clear();
    }

    public A getRandom() {
        if (this.isEmpty()) {
            return null;
        }

        return this.treeMap.get(this.treeMap.ceilingKey(ThreadLocalRandom.current().nextDouble() * this.totalWeight));
    }

    public double getTotalWeight() {
        return this.totalWeight;
    }
}
