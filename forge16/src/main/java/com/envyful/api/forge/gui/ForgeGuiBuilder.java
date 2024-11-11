package com.envyful.api.forge.gui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.gui.close.ForgeCloseConsumer;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.PlayerManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Builder implementation for the ForgeGui
 *
 */
public class ForgeGuiBuilder implements Gui.Builder {

    private ITextComponent title;
    private int height = 5;
    private PlayerManager<ForgeEnvyPlayer, ServerPlayerEntity> playerManager;
    private ForgeCloseConsumer closeConsumer =
            (ForgeCloseConsumer) GuiFactory.empty();

    private final List<Pane> panes = new ArrayList<>();

    @Override
    public Gui.Builder title(Object title) {
        if (title instanceof ITextComponent) {
            this.title = (ITextComponent) title;
        } else if (title instanceof String) {
            this.title = UtilChatColour.colour((String) title);
        } else {
            throw new IllegalArgumentException("Unsupported title type given");
        }

        return this;
    }

    @Override
    public Gui.Builder height(int height) {
        this.height = height;
        return this;
    }

    @Override
    public Gui.Builder addPane(Pane pane) {
        this.panes.add(pane);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Gui.Builder setPlayerManager(PlayerManager<?, ?> playerManager) {
        this.playerManager =
                (PlayerManager<ForgeEnvyPlayer, ServerPlayerEntity>)
                        playerManager;
        return this;
    }

    @Override
    public Gui.Builder closeConsumer(CloseConsumer<?, ?> closeConsumer) {
        this.closeConsumer = (ForgeCloseConsumer)closeConsumer;
        return this;
    }

    @Override
    public Gui build() {
        if (this.playerManager == null) {
            throw new IllegalArgumentException(
                    "Cannot build GUI without PlayerManager being set"
            );
        }

        return new ForgeGui(
                this.title, this.height,
                this.closeConsumer, this.panes.toArray(new Pane[0])
        );
    }
}
