package com.envyful.api.math;

import org.junit.jupiter.api.Test;

public class RandomWeightedSetTests {

    // Checks that the weights are working correctly - roughly close to 1,000 (1% of 100,000)
    @Test
    void statisticsCheckOne() {
        RandomWeightedSet<String> randomWeightedSet = new RandomWeightedSet<>("hello1", 1)
                .add("hello2", 99);
        int count = 0;

        for (int i = 0; i < 100_000; ++i) {
            if (randomWeightedSet.getRandom().equalsIgnoreCase("hello1")) {
                ++count;
            }
        }

        assert count >= 950;
    }

    // Checks that the weights are working correctly - roughly close to 1,000 (1% of 100,000)
    @Test
    void statisticsCheckTwo() {
        RandomWeightedSet<String> randomWeightedSet = new RandomWeightedSet<>("hello1", 1)
                .add("hello2", 80)
                .add("hello3", 19);
        int count = 0;

        for (int i = 0; i < 100_000; ++i) {
            if (randomWeightedSet.getRandom().equalsIgnoreCase("hello1")) {
                ++count;
            }
        }

        assert count >= 950;
    }


    // Checks that the weights are summing correctly
    @Test
    void totalWeightCheck() {
        RandomWeightedSet<String> randomWeightedSet = new RandomWeightedSet<>("hello1", 1)
                .add("hello2", 80)
                .add("hello3", 19);
        assert randomWeightedSet.getTotalWeight() == 100;
    }

}
