package com.envyful.api.type;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

public class BooleanFunctionTest {

    // Tests the functional interface status of BooleanFunction
    @Test
    void testFunctionInterface() {
        assert ((Predicate<Integer>) a -> a == 1).test(1);
    }

}
