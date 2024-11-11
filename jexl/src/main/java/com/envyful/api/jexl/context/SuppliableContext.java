package com.envyful.api.jexl.context;

import org.apache.commons.jexl3.JexlContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SuppliableContext implements JexlContext {

    private final Map<String, Object> context = new HashMap<>();

    public SuppliableContext() {
    }

    @Override
    public Object get(String s) {
        var object = this.context.get(s);

        if (object instanceof Supplier) {
            return ((Supplier<?>) object).get();
        }

        return object;
    }

    public void set(String s, Supplier<Object> supplier) {
        this.context.put(s, supplier);
    }

    @Override
    public void set(String s, Object o) {
        this.context.put(s, o);
    }

    @Override
    public boolean has(String s) {
        return this.context.containsKey(s);
    }
}
