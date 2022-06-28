package com.envyful.api.discord.button;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * Config serializable entry for a Discord button with optional Emoji
 *
 */
@ConfigSerializable
public class ConfigurableEmojiButton extends ConfigurableButton {

    private boolean unicode = false;
    private boolean markdown = false;
    private String emojiId;
    private transient Emoji emoji = null;

    public ConfigurableEmojiButton(String idOrUrl, ButtonStyle style, String label, String emojiId) {
        super(idOrUrl, style, label);

        this.emojiId = emojiId;
    }

    public ConfigurableEmojiButton() {
        super();
    }

    public Emoji getEmoji(JDA jda) {
        if (this.unicode) {
            return Emoji.fromUnicode(this.emojiId);
        }

        if (this.markdown) {
            return Emoji.fromMarkdown(this.emojiId);
        }

        return Emoji.fromEmote(jda.getEmoteById(Long.parseLong(this.emojiId)));
    }

    @Override
    public Button create(JDA jda) {
        return super.create(jda)
                .withEmoji(this.getEmoji(jda));
    }
}
