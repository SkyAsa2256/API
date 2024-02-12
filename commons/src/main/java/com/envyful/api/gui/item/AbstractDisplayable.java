package com.envyful.api.gui.item;

import com.envyful.api.gui.pane.Pane;

public abstract class AbstractDisplayable implements Displayable {

    @Override
    public void add(Pane pane, int slot) {
        pane.set(slot, this);
    }

    @Override
    public void add(Pane pane, int slotX, int slotY) {
        pane.set(slotX, slotY, this);
    }
}
