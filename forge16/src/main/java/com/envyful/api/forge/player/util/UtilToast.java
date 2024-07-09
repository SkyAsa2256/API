package com.envyful.api.forge.player.util;

import com.envyful.api.config.ConfigToast;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.text.Placeholder;
import com.google.common.base.Preconditions;
import net.minecraft.advancements.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * UtilToast -
 * A class for sending toast messages to players.
 *
 */
public class UtilToast {

    private static final ResourceLocation TESTER = new ResourceLocation("mia", "tester");

    public static void sendToast(ServerPlayerEntity player, ConfigToast toast, Placeholder... placeholders) {
        sendToast(player, FrameType.valueOf(toast.getType()),
                UtilChatColour.colour(toast.getMessage(), placeholders),
                UtilConfigItem.fromConfigItem(toast.getItem(), placeholders));
    }

    public static void sendToast(ServerPlayerEntity player, FrameType frameType, ITextComponent message, ItemStack display) {
        var displayInfo = new DisplayInfo(
                display,
                message, // \n indicates a new line
                new StringTextComponent(""), // I think this only gets used in the advancement display UI
                null, // This is only used in the advancement display UI
                frameType, // FrameType.CHALLENGE, FrameType.GOAL, FrameType.TASK changes the text displayed at the top (if a single line) or at the start (if multiline)
                true, // Whether to show a toast message
                false, // Whether to announce in chat - I don't think this matters here?
                true // Whether to hide the advancement in the advancement display UI
        );

        var advancement = new Advancement(
                TESTER, null,
                displayInfo,
                AdvancementRewards.EMPTY,
                Map.of("test", new Criterion()),
                new String[][]{{"test"}}
        );

        var progress = new AdvancementProgress();
        progress.update(
                Map.of("test", new Criterion()),
                new String[][]{{"test"}}
        );

        progress.grantProgress("test");

        player.connection.send(new SAdvancementInfoPacket(
                false, List.of(advancement), Set.of(), Map.of(
                advancement.getId(), progress
        )));
    }

    public static ToastBuilder builder() {
        return new ToastBuilder();
    }

    public static class ToastBuilder {

        private ITextComponent message;
        private ItemStack display;
        private FrameType frameType;

        public ToastBuilder message(ITextComponent message) {
            this.message = message;
            return this;
        }

        public ToastBuilder display(ItemStack display) {
            this.display = display;
            return this;
        }

        public ToastBuilder frameType(FrameType frameType) {
            this.frameType = frameType;
            return this;
        }

        public void send(ServerPlayerEntity... players) {
            Preconditions.checkNotNull(message, "Message cannot be null");
            Preconditions.checkNotNull(display, "Display cannot be null");
            Preconditions.checkNotNull(frameType, "FrameType cannot be null");

            for (var player : players) {
                if (player == null) {
                    continue;
                }
                UtilToast.sendToast(player, frameType, message, display);
            }
        }
    }
}
