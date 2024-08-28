package com.envyful.api.type.timed;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ImmutableTimedValue<T> implements Timed<T> {

    private final T value;
    private final Instant timestamp;
    private final long expiry;

    public ImmutableTimedValue(T value, long expiry, TimeUnit timeUnit) {
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
        throw new UnsupportedOperationException("Cannot set value on an immutable timed value");
    }

    @Override
    public void setExpiry(long expiry, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("Cannot set expiry on an immutable timed value");
    }

    @Override
    public long getExpiry(TimeUnit timeUnit) {
        return timeUnit.convert(this.expiry, TimeUnit.MILLISECONDS);
    }
}
