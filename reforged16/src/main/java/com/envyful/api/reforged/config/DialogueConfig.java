package com.envyful.api.reforged.config;

import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class DialogueConfig {

    private String title;
    private String body;

    public DialogueConfig(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public DialogueConfig() {
    }

    public void apply(Dialogue.DialogueBuilder builder) {
        if (builder != null) {
            builder.setText(this.body)
                    .setName(this.title);
        }
    }
}
