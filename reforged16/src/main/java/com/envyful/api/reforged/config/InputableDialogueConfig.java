package com.envyful.api.reforged.config;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueInputScreen;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

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

    public void apply(DialogueInputRegistry.Builder builder) {
        builder
                .title(UtilChatColour.colour(this.title))
                .text(UtilChatColour.colour(this.text))
                .defaultInputValue(this.defaultInput);

        if (this.shouldCloseOnEscape) {
            builder.closeOnEscape();
        } else {
            builder.notCloseable();
        }
    }
}
