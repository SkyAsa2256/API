package com.envyful.api.forge.gui.pane;

import com.envyful.api.forge.gui.item.ForgeStaticDisplayable;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.type.Pair;
import com.google.common.collect.Lists;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

/**
 *
 * Simple implementation of the {@link Pane} interface where the height and width of the pane are unchanging.
 *
 */
public class ForgeStaticPane implements Pane {

    private final int topLeftX;
    private final int topLeftY;
    private final int width;
    private final int height;
    private final ForgeStaticPaneDisplayable[][] items;

    private boolean full = false;
    private Pair<Integer, Integer> lastPos = Pair.of(0, 0);

    private ForgeStaticPane(int topLeftX, int topLeftY, int height, int width) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.width = width;
        this.height = height;
        this.items = new ForgeStaticPaneDisplayable[width][height];
    }

    @Override
    public void add(Displayable displayable) {
        if (this.full) {
            return;
        }

        this.items[this.lastPos.getX()][this.lastPos.getY()] = new ForgeStaticPaneDisplayable(this, displayable,
                this.lastPos.getX(), this.lastPos.getY());

        if (this.width == (this.lastPos.getX() + 1)) {
            if (this.height == (this.lastPos.getY() + 1)) {
                this.full = true;
                return;
            }

            this.lastPos = Pair.of(0, this.lastPos.getY() + 1);
        } else {
            this.lastPos = Pair.of(this.lastPos.getX() + 1, this.lastPos.getY());
        }
    }

    @Override
    public void set(int posX, int posY, Displayable displayable) {
        if (posX >= this.width) {
            throw new RuntimeException("Cannot set an X position greater than the width");
        }

        if (posY >= this.height) {
            throw new RuntimeException("Cannot set an Y position greater than the height");
        }

        this.items[posX][posY] = new ForgeStaticPaneDisplayable(this, displayable, posX, posY);
    }

    @Override
    public void set(int pos, Displayable displayable) {
        this.set(pos % (this.width), pos % (this.height), displayable);
    }

    @Override
    public void fill(Displayable displayable) {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.set(x, y, displayable);
            }
        }
    }

    @Override
    public void clear() {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.set(x, y, null);
            }
        }
    }

    public List<Slot> getSlots() {
        List<Slot> slots = Lists.newArrayList();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                ForgeStaticPaneDisplayable item = this.items[x][y];

                if (item == null || item.getDisplayable() == null) {
                    continue;
                }

                slots.add(item);
            }
        }

        return slots;
    }

    public NonNullList<ItemStack> getDisplayItems() {
        NonNullList<ItemStack> display = NonNullList.create();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                ForgeStaticPaneDisplayable item = this.items[x][y];

                if (item == null || item.getDisplayable() == null) {
                    continue;
                }

                display.add(ForgeStaticDisplayable.Converter
                        .toNative((ForgeStaticDisplayable) item.getDisplayable()));
            }
        }

        return display;
    }

    public boolean inPane(int xPos, int yPos) {
        if (xPos < this.topLeftX || yPos < this.topLeftY) {
            return false;
        }

        return yPos <= (this.topLeftY + this.height) && xPos <= (this.topLeftX + this.width);
    }

    public static final class ForgeStaticPaneDisplayable extends Slot {

        private final Displayable displayable;

        public ForgeStaticPaneDisplayable(ForgeStaticPane pane, Displayable displayable, int xPosition, int yPosition) {
            super(null, xPosition + (9 * yPosition), pane.topLeftX + xPosition,
                    pane.topLeftY + yPosition);

            this.displayable = displayable;
        }

        public Displayable getDisplayable() {
            return this.displayable;
        }
    }

    public static final class Builder implements Pane.Builder {

        private int topLeftX = 0;
        private int topLeftY = 0;
        private int width = 9;
        private int height = 5;

        public Builder() {}

        @Override
        public Pane.Builder topLeftX(int topLeftX) {
            this.topLeftX = topLeftX;
            return this;
        }

        @Override
        public Pane.Builder topLeftY(int topLeftY) {
            this.topLeftY = topLeftY;
            return this;
        }

        @Override
        public Pane.Builder width(int width) {
            this.width = width;
            return this;
        }

        @Override
        public Pane.Builder height(int height) {
            this.height = height;
            return this;
        }

        @Override
        public Pane build() {
            return new ForgeStaticPane(this.width, this.height, this.topLeftX, this.topLeftY);
        }
    }
}
