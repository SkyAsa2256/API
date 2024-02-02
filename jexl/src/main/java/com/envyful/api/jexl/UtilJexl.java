package com.envyful.api.jexl;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;

public class UtilJexl {

    private static final JexlEngine ENGINE = new JexlBuilder().create();

    public static final JexlEngine getEngine() {
        return ENGINE;
    }
}
