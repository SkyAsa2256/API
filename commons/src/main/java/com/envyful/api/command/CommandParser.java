package com.envyful.api.command;

import com.envyful.api.command.exception.CommandParseException;

public interface CommandParser<A extends PlatformCommand<B>, B> {

    A parseCommand(Object o) throws CommandParseException;

}
