package com.envyful.api.forge.command.parser;

import com.envyful.api.command.CommandFactory;
import com.envyful.api.command.annotate.AnnotationCommandParser;
import com.envyful.api.forge.command.command.ForgePlatformCommand;
import net.minecraft.command.ICommandSource;

public class ForgeAnnotationCommandParser extends AnnotationCommandParser<ForgePlatformCommand, ICommandSource> {

    public ForgeAnnotationCommandParser(CommandFactory<?, ICommandSource> commandFactory) {
        super(commandFactory, ICommandSource.class);
    }
}
