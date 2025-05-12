package com.envyful.api.neoforge.player.util;

import com.envyful.api.config.ConfigToast;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * UtilToast -
 * A class for sending toast messages to players.
 *
 */
public class UtilToast {

    private static final ResourceLocation TESTER = ResourceLocation.tryParse("envyapi:tester");

    public static void sendToast(ServerPlayer player, ConfigToast toast, Placeholder... placeholders) {
        sendToast(player, AdvancementType.valueOf(toast.getType()),
                PlatformProxy.<Component>parse(toast.getMessage(), placeholders).get(0),
                UtilConfigItem.fromConfigItem(toast.getItem(), placeholders));
    }

    public static void sendToast(ServerPlayer player, AdvancementType frameType, Component message, ItemStack display) {
        var displayInfo = new DisplayInfo(
                display,
                message, // \n indicates a new line
               Component.empty(), // I think this only gets used in the advancement display UI
                null, // This is only used in the advancement display UI
                frameType, // FrameType.CHALLENGE, FrameType.GOAL, FrameType.TASK changes the text displayed at the top (if a single line) or at the start (if multiline)
                true, // Whether to show a toast message
                false, // Whether to announce in chat - I don't think this matters here?
                true // Whether to hide the advancement in the advancement display UI
        );

        var advancement = new Advancement(
                Optional.empty(),
                Optional.of(displayInfo),
                AdvancementRewards.EMPTY,
                Map.of("test", new Criterion<>(CriteriaTriggers.IMPOSSIBLE, new ImpossibleTrigger.TriggerInstance())),
                new AdvancementRequirements(List.of(List.of("test"))),
                false
        );

        var progress = new AdvancementProgress();
        progress.update(new AdvancementRequirements(List.of(List.of("test"))));

        progress.grantProgress("test");

        player.connection.send(new ClientboundUpdateAdvancementsPacket(
                false, List.of(new AdvancementHolder(TESTER, advancement)), Set.of(), Map.of(
                TESTER, progress
        )));
    }

    public static ToastBuilder builder() {
        return new ToastBuilder();
    }

    public static class ToastBuilder {

        private Component message;
        private ItemStack display;
        private AdvancementType frameType;

        public ToastBuilder message(Component message) {
            this.message = message;
            return this;
        }

        public ToastBuilder display(ItemStack display) {
            this.display = display;
            return this;
        }

        public ToastBuilder frameType(AdvancementType frameType) {
            this.frameType = frameType;
            return this;
        }

        public void send(ServerPlayer... players) {
            assert message != null : "Message cannot be null";
            assert display != null : "Display cannot be null";
            assert frameType != null : "FrameType cannot be null";

            for (var player : players) {
                if (player == null) {
                    continue;
                }
                UtilToast.sendToast(player, frameType, message, display);
            }
        }
    }
}
