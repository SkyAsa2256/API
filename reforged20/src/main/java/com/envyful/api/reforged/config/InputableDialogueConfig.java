package com.envyful.api.reforged.config;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueInputScreen;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;

@ConfigSerializable
public class InputableDialogueConfig {

    private String title;
    private String text;
    private String defaultInput;
    private boolean shouldCloseOnEscape;

    public InputableDialogueConfig(String title, String text, String defaultInput, boolean shouldCloseOnEscape) {
        this.title = title;
        this.text = text;
        this.defaultInput = defaultInput;
        this.shouldCloseOnEscape = shouldCloseOnEscape;
    }

    public InputableDialogueConfig() {
    }

    public DialogueInputScreen.Builder create() {
        return new DialogueInputScreen.Builder()
                .setTitle(UtilChatColour.colour(this.title))
                .setText(UtilChatColour.colour(this.text))
                .setDefaultText(this.defaultInput)
                .setShouldCloseOnEsc(this.shouldCloseOnEscape);
    }

    public void apply(DialogueInputRegistry.Builder builder, Placeholder... placeholders) {
        builder
                .title(UtilChatColour.colour(PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.title), placeholders).get(0)))
                .text(UtilChatColour.colour(PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.text), placeholders).get(0)))
                .defaultInputValue(PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.defaultInput), placeholders).get(0));

        if (this.shouldCloseOnEscape) {
            builder.closeOnEscape();
        } else {
            builder.notCloseable();
        }
    }
}
