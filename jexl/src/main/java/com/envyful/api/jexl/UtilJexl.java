package com.envyful.api.jexl;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;

import java.util.Map;

/**
 *
 * Static utility class for Jexl storing an instance of the JexlEngine
 *
 */
public class UtilJexl {

    private static final JexlEngine ENGINE = new JexlBuilder()
            .namespaces(Map.of(
                    "mth", Math.class
            ))
            .create();

    public static final JexlEngine getEngine() {
        return ENGINE;
    }
}
