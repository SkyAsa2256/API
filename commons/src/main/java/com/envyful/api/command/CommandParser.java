package com.envyful.api.command;

import com.envyful.api.command.exception.CommandParseException;

/**
 *
 * An interface for parsing an object into a command.
 * <br>
 * Currently, the only implementation that exists is for
 * annotation based commands {@link com.envyful.api.command.annotate.AnnotationCommandParser}
 *
 * @param <A> The command type
 * @param <B> The sender type
 */
public interface CommandParser<A extends PlatformCommand<B>, B> {

    /**
     *
     * The method for parsing an object into a command
     *
     * @param o The object being parsed
     * @return The parsed command
     * @throws CommandParseException Thrown if there is an error parsing the object
     */
    A parseCommand(Object o) throws CommandParseException;

}
