package com.envyful.api.text;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class UtilStringTests {

    // Checks that shuffle is working by ensuring that the shuffled value isn't the same as the original value at least once
    @Test
    void shuffleStringTest() {
        int shuffleSuccess = 0;
        String value = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < 10; i++) {
            if (!value.equals(UtilString.shuffle(value))) {
                ++shuffleSuccess;
            }
        }

        assert shuffleSuccess >= 1;
    }

    // Checks the mapping, and joining, is working correctly
    @Test
    void joinStringTest() {
        List<RandomTestObject> objects = Lists.newArrayList(
                new RandomTestObject("a"),
                new RandomTestObject("b"),
                new RandomTestObject("c"),
                new RandomTestObject("d"),
                new RandomTestObject("e")
        );

        assert UtilString.join(objects, "", RandomTestObject::toString).equalsIgnoreCase("abcde");
    }

    // Checks the mapping, joining, and delimiter is working correctly
    @Test
    void joinStringTestWithDelimiter() {
        List<RandomTestObject> objects = Lists.newArrayList(
                new RandomTestObject("a"),
                new RandomTestObject("b"),
                new RandomTestObject("c"),
                new RandomTestObject("d"),
                new RandomTestObject("e")
        );

        assert UtilString.join(objects, ",", RandomTestObject::toString).equalsIgnoreCase("a,b,c,d,e");
    }

    // Checks that when provided an empty list the matching list is also empty
    @Test
    void checkEmptyList() {
        assert UtilString.getMatching("test1", Collections.emptyList()).isEmpty();
    }

    // Checks that the begins with check is working
    @Test
    void checkOneEntry() {
        assert UtilString.getMatching("test1", Lists.newArrayList(
                "test12341",
                "test"
        )).isEmpty();
    }

    // Checks that the begins with check is working and is case insensitive
    @Test
    void checkCaseInsensitive() {
        assert UtilString.getMatching("tesT1", Lists.newArrayList(
                "test12341",
                "test"
        )).isEmpty();
    }

    class RandomTestObject {

        private final String value;

        private RandomTestObject(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
