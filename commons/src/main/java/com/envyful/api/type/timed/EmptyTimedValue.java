package com.envyful.api.type.timed;

import java.util.concurrent.TimeUnit;

/**
 *
 * An empty implementation of the {@link Timed} interface
 *
 * @param <T> the type of the value
 */
public class EmptyTimedValue<T> implements Timed<T> {

    @Override
    public T get() {
        return null;
    }

    @Override
    public void set(T t) {

    }

    @Override
    public void setExpiry(long expiry, TimeUnit timeUnit) {

    }

    @Override
    public long getExpiry(TimeUnit timeUnit) {
        return -1;
    }
}
