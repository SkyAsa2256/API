package com.envyful.api.forge.gui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * Builder implementation for the ForgeGui
 *
 */
public class ForgeGuiBuilder implements Gui.Builder {

    private ITextComponent title;
    private int height = 5;
    private List<Pane> panes = Lists.newArrayList();
    private PlayerManager<ForgeEnvyPlayer, EntityPlayerMP> playerManager;
    private Consumer<EnvyPlayer<?>> closeConsumer = null;

    @Override
    public Gui.Builder title(Object title) {
        if (title instanceof ITextComponent) {
            this.title = (ITextComponent) title;
        } else if (title instanceof String) {
            this.title = new TextComponentString(UtilChatColour.translateColourCodes('&', (String) title));
        } else {
            throw new RuntimeException("Unsupported title type given");
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

    @Override
    public Gui.Builder setPlayerManager(PlayerManager<?, ?> playerManager) {
        this.playerManager = (PlayerManager<ForgeEnvyPlayer, EntityPlayerMP>) playerManager;
        return this;
    }

    @Override
    public Gui.Builder setCloseConsumer(Consumer<EnvyPlayer<?>> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    @Override
    public Gui build() {
        if (this.playerManager == null) {
            throw new RuntimeException("Cannot build GUI without PlayerManager being set");
        }

        return new ForgeGui(this.title, this.height, this.playerManager,
                forgeEnvyPlayer -> this.closeConsumer.accept(forgeEnvyPlayer), this.panes.toArray(new Pane[0]));
    }
}
