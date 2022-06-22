package com.envyful.api.command.sender;

/**
 *
 * Represents a type of command sender
 *
 * @param <A> The impl type
 * @param <B> The custom type
 */
public interface SenderType<A, B> {

    Class<?> getType();

    B getInstance(A sender);

}
