package com.envyful.api.spigot.chat;

import com.envyful.api.platform.text.TextFormatter;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public class MiniMessageTextFormatter implements TextFormatter<Component> {

    @Override
    public List<Component> parse(List<String> text, Placeholder... placeholders) {
        List<Component> components = new ArrayList<>();

        for (String s : text) {
            for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(s, placeholders)) {
                components.add(MiniMessage.miniMessage().deserialize(handlePlaceholder));
            }
        }

        return components;
    }

    @Override
    public Component parse(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    @Override
    public String unresolve(Component text) {
        return MiniMessage.miniMessage().serialize(text);
    }

    @Override
    public String strip(String text) {
        return MiniMessage.miniMessage().stripTags(text);
    }
}
