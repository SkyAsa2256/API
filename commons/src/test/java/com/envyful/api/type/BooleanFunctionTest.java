package com.envyful.api.type;

import org.junit.jupiter.api.Test;

public class BooleanFunctionTest {

    // Tests the functional interface status of BooleanFunction
    @Test
    void testFunctionInterface() {
        assert ((BooleanFunction<Integer>) a -> a == 1).test(1);
    }

}
