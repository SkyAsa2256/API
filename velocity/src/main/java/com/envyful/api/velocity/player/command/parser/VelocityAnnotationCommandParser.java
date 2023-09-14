package com.envyful.api.velocity.player.command.parser;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.annotate.AnnotationCommandParser;
import com.envyful.api.velocity.player.command.command.VelocityPlatformCommand;
import com.velocitypowered.api.command.CommandSource;

public class VelocityAnnotationCommandParser extends AnnotationCommandParser<VelocityPlatformCommand, CommandSource> {

    public VelocityAnnotationCommandParser(CommandFactory<?, CommandSource> commandFactory) {
        super(commandFactory, CommandSource.class);
    }
}
