package com.envyful.api.jexl.config;

import com.envyful.api.jexl.UtilJexl;
import org.apache.commons.jexl3.JexlExpression;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * A simple configuration object for a Jexl calculation
 *
 */
@ConfigSerializable
public class CalculationConfig {

    private String calculation;
    private transient JexlExpression expression;

    public CalculationConfig() {
    }

    public CalculationConfig(String calculation) {
        this.calculation = calculation;
    }

    public JexlExpression getExpression() {
        if (this.calculation == null || this.calculation.isEmpty()) {
            return null;
        }

        if (this.expression == null) {
            this.expression = UtilJexl.getEngine().createExpression(this.calculation);
        }

        return this.expression;
    }

}
