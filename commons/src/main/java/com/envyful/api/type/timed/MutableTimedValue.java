package com.envyful.api.type.timed;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MutableTimedValue<T> implements Timed<T> {

    private T value;
    private Instant timestamp;
    private long expiry;

    public MutableTimedValue() {
        this(null);
    }

    public MutableTimedValue(T value) {
        this(value, 30, TimeUnit.SECONDS);
    }

    public MutableTimedValue(T value, long expiry, TimeUnit timeUnit) {
        this.value = value;
        this.timestamp = Instant.now();
        this.expiry = timeUnit.toMillis(expiry);
    }

    @Override
    public T get() {
        if (this.timestamp.plusMillis(this.expiry).isBefore(Instant.now())) {
            return null;
        }

        return this.value;
    }

    @Override
    public void set(T t) {
        this.value = t;
        this.timestamp = Instant.now();
    }

    @Override
    public void setExpiry(long expiry, TimeUnit timeUnit) {
        this.expiry = timeUnit.toMillis(expiry);
    }

    @Override
    public long getExpiry(TimeUnit timeUnit) {
        return timeUnit.convert(this.expiry, TimeUnit.MILLISECONDS);
    }
}
