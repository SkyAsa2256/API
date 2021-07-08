package com.envyful.api.forge.gui;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.PlayerManager;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

/**
 *
 * Builder implementation for the ForgeGui
 *
 */
public class ForgeGuiBuilder implements Gui.Builder {

    private String title;
    private int height = 5;
    private List<Pane> panes = Lists.newArrayList();
    private PlayerManager<ForgeEnvyPlayer, EntityPlayerMP> playerManager;

    @Override
    public Gui.Builder title(String title) {
        this.title = title;
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
    public Gui build() {
        if (this.playerManager == null) {
            throw new RuntimeException("Cannot build GUI without PlayerManager being set");
        }

        return new ForgeGui(this.title, this.height, this.playerManager, this.panes.toArray(new Pane[0]));
    }
}
