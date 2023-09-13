package com.envyful.api.forge.command.parser;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.annotate.AnnotationCommandParser;
import com.envyful.api.forge.command.command.ForgePlatformCommand;
import net.minecraft.commands.CommandSource;

public class ForgeAnnotationCommandParser extends AnnotationCommandParser<ForgePlatformCommand, CommandSource> {

    public ForgeAnnotationCommandParser(CommandFactory<?, CommandSource> commandFactory) {
        super(commandFactory, CommandSource.class);
    }
}
