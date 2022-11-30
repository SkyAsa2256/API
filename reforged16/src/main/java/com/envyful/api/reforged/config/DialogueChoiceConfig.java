package com.envyful.api.reforged.config;

import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class DialogueChoiceConfig {

    private String text;

    public DialogueChoiceConfig(String text) {
        this.text = text;
    }

    public DialogueChoiceConfig() {
    }

    public void apply(Choice.ChoiceBuilder builder) {
        if (builder != null) {
            builder.setText(this.text);
        }
    }
}
