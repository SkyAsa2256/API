package com.envyful.api.type.timed;

import java.util.concurrent.TimeUnit;

/**
 *
 * An interface representing a value that has an expiry time
 * <br>
 * Calling the {@link #get()} method will return the value if it has not expired and null if it has
 * <br>
 * Calling the {@link #set(Object)} method will set the value and record the time at which it was set
 * so that the {@link #get()} method can determine if it has expired
 * <br>
 * Calling the {@link #setExpiry(long, TimeUnit)} method will set the expiry time of the value
 * <br>
 * Calling the {@link #getExpiry(TimeUnit)} method will return the expiry time in the specified time unit
 *
 * @param <T> the type of the value
 */
public interface Timed<T> {

    T get();

    void set(T t);

    void setExpiry(long expiry, TimeUnit timeUnit);

    long getExpiry(TimeUnit timeUnit);

    /**
     *
     * Factory method for creating a new {@link Timed} instance
     *
     * @param value the value
     * @param <A> the type of the value
     * @return the new {@link Timed} instance
     */
    static <A> Timed<A> mutable(A value) {
        return new MutableTimedValue<>(value);
    }

    /**
     *
     * Factory method for creating a new {@link Timed} instance
     *
     * @param value the value
     * @param expiry the expiry time
     * @param timeUnit the time unit of the expiry time
     * @param <A> the type of the value
     * @return the new {@link Timed} instance
     */
    static <A> Timed<A> mutable(A value, long expiry, TimeUnit timeUnit) {
        return new MutableTimedValue<>(value, expiry, timeUnit);
    }

    /**
     *
     * Factory method for creating a new immutable {@link Timed} instance
     *
     * @param value the value
     * @param <A> the type of the value
     * @return the new immutable {@link Timed} instance
     */
    static <A> Timed<A> immutable(A value, long expiry, TimeUnit timeUnit) {
        return new ImmutableTimedValue<>(value, expiry, timeUnit);
    }

    /**
     *
     * Adds a new empty {@link Timed} instance
     *
     * @return the new empty {@link Timed} instance
     * @param <A> the type of the value
     */
    static <A> Timed<A> empty() {
        return new EmptyTimedValue<>();
    }
}
