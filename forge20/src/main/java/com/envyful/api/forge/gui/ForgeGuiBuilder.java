package com.envyful.api.forge.gui;

import com.envyful.api.forge.gui.close.ForgeCloseConsumer;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Builder implementation for the ForgeGui
 *
 */
public class ForgeGuiBuilder implements Gui.Builder {

    private Component title;
    private int height = 5;
    private ForgeCloseConsumer closeConsumer =
            (ForgeCloseConsumer) GuiFactory.empty();

    private final List<Pane> panes = new ArrayList<>();

    @Override
    public Gui.Builder title(Object title) {
        if (title instanceof Component) {
            this.title = (Component) title;
        } else if (title instanceof String) {
            this.title = PlatformProxy.parse((String) title);
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

    @Override
    public Gui.Builder closeConsumer(CloseConsumer<?, ?> closeConsumer) {
        this.closeConsumer = (ForgeCloseConsumer)closeConsumer;
        return this;
    }

    @Override
    public Gui build() {
        return new ForgeGui(
                this.title, this.height,
                this.closeConsumer, this.panes.toArray(new Pane[0])
        );
    }
}
