package com.envyful.api.spigot.gui;

import com.envyful.api.gui.Gui;
import com.envyful.api.gui.close.CloseConsumer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.spigot.gui.close.SpigotCloseConsumer;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Builder implementation for the SpigotGui
 *
 */
public class SpigotGuiBuilder implements Gui.Builder {

    private Component title;
    private int height = 5;
    private List<Pane> panes = new ArrayList<>();
    private SpigotCloseConsumer closeConsumer = (SpigotCloseConsumer) GuiFactory.closeConsumerBuilder().build();

    @Override
    public Gui.Builder title(Object title) {
        if (title instanceof Component component) {
            this.title = component;
        } else if (title instanceof String text) {
            this.title = PlatformProxy.parse(text);
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
    public Gui.Builder closeConsumer(CloseConsumer<?, ?> closeConsumer) {
        this.closeConsumer = (SpigotCloseConsumer) closeConsumer;
        return this;
    }

    @Override
    public Gui build() {
        return new SpigotGui(this.title, this.height, this.closeConsumer, this.panes.toArray(new Pane[0]));
    }
}
