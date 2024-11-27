package com.envyful.api.forge.chat;

import com.envyful.api.platform.text.TextFormatter;
import com.envyful.api.text.Placeholder;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ComponentTextFormatter implements TextFormatter<Component> {

    private static final ComponentTextFormatter INSTANCE = new ComponentTextFormatter();

    private ComponentTextFormatter() {}

    @Override
    public List<Component> parse(List<String> text, Placeholder... placeholders) {
        return UtilChatColour.colour(text, placeholders);
    }

    @Override
    public Component parse(String text) {
        return UtilChatColour.colour(text);
    }

    public static ComponentTextFormatter getInstance() {
        return INSTANCE;
    }
}
