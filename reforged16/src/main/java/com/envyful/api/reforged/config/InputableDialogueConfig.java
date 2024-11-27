package com.envyful.api.reforged.config;

import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueInputScreen;
import net.minecraft.util.text.ITextComponent;
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
                .setTitle(PlatformProxy.<ITextComponent>parse(this.title))
                .setText(PlatformProxy.<ITextComponent>parse(this.text))
                .setDefaultText(this.defaultInput)
                .setShouldCloseOnEsc(this.shouldCloseOnEscape);
    }

    public void apply(DialogueInputRegistry.Builder builder, Placeholder... placeholders) {
        builder
                .title(PlatformProxy.parse(PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.title), placeholders).get(0)))
                .text(PlatformProxy.parse(PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.text), placeholders).get(0)))
                .defaultInputValue(PlaceholderFactory.handlePlaceholders(Collections.singletonList(this.defaultInput), placeholders).get(0));

        if (this.shouldCloseOnEscape) {
            builder.closeOnEscape();
        } else {
            builder.notCloseable();
        }
    }
}
