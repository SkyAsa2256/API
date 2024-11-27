package com.envyful.api.forge.chat;

import com.envyful.api.platform.text.TextFormatter;
import com.envyful.api.text.Placeholder;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class ITextComponentTextFormatter implements TextFormatter<ITextComponent> {

    private static final ITextComponentTextFormatter INSTANCE = new ITextComponentTextFormatter();

    private ITextComponentTextFormatter() {}

    @Override
    public List<ITextComponent> parse(List<String> text, Placeholder... placeholders) {
        return UtilChatColour.colour(text, placeholders);
    }

    @Override
    public ITextComponent parse(String text) {
        return UtilChatColour.colour(text);
    }

    @Override
    public String unresolve(ITextComponent text) {
        return text.getString();
    }

    @Override
    public String strip(String text) {
        return UtilChatColour.stripColor(text);
    }

    public static ITextComponentTextFormatter getInstance() {
        return INSTANCE;
    }
}
