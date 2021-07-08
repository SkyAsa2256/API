package com.envyful.api.forge.gui;

import com.envyful.api.forge.gui.pane.ForgeStaticPane;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

/**
 *
 * Forge implementation of the {@link Gui} interface.
 *
 */
public class ForgeGui implements Gui {

    private final ITextComponent title;
    private final int height;
    private final PlayerManager<ForgeEnvyPlayer, EntityPlayerMP> playerManager;
    private final ForgeStaticPane[] panes;

    private ForgeGui(String title, int height, PlayerManager<ForgeEnvyPlayer, EntityPlayerMP> playerManager, Pane... panes) {
        this.title = new TextComponentString(title);
        this.height = height;
        this.playerManager = playerManager;
        this.panes = new ForgeStaticPane[panes.length];
        int i = 0;

        for (Pane pane : panes) {
            if (!(pane instanceof ForgeStaticPane)) {
                continue;
            }

            panes[i] = pane;
            ++i;
        }
    }

    @Override
    public void open(EnvyPlayer<?> player) {
        if (!(player instanceof ForgeEnvyPlayer)) {
            return;
        }

        EntityPlayerMP parent = ((ForgeEnvyPlayer) player).getParent();
        ForgeGuiContainer container = new ForgeGuiContainer(this);

        parent.closeContainer();
        parent.openContainer = container;
        parent.currentWindowId = 1;
        parent.connection.sendPacket(new SPacketOpenWindow(parent.currentWindowId, "minecraft:container", this.title, 54));
        container.detectAndSendChanges();
        parent.sendAllContents(container, container.inventoryItemStacks);
    }

    /**
     *
     * Forge container class for the GUI
     *
     */
    private final class ForgeGuiContainer extends Container {

        private final ForgeGui gui;

        public ForgeGuiContainer(ForgeGui gui) {
            this.gui = gui;

            this.update(this.gui.panes);
        }

        public void update(ForgeStaticPane[] panes) {
            List<Slot> slots = Lists.newArrayList();
            NonNullList<ItemStack> display = NonNullList.create();

            for (ForgeStaticPane pane : panes) {
                slots.addAll(pane.getSlots());
                display.addAll(pane.getDisplayItems());
            }

            for (int i = 9; i < 36; i++) {
                inventorySlots.add(new Slot(null, i - 9, 0, 0));
                inventoryItemStacks.add(ItemStack.EMPTY);
            }

            for (int i = 0; i < 9; i++) {
                inventorySlots.add(new Slot(null, i + 27, 0, 0));
                inventoryItemStacks.add(ItemStack.EMPTY);
            }

            this.inventorySlots = slots;
            this.inventoryItemStacks = display;
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }

        @Override
        public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, EntityPlayer player) {
            if (slot <= -1) {
                return ItemStack.EMPTY;
            }

            Displayable.ClickType clickType = this.convertClickType(clickTypeIn);

            if (clickType == null) {
                return ItemStack.EMPTY;
            }

            EnvyPlayer<?> envyPlayer = this.gui.playerManager.getPlayer((EntityPlayerMP) player);

            if (envyPlayer == null) {
                return ItemStack.EMPTY;
            }

            int xPos = slot / 9;
            int yPos = slot % 9;

            for (ForgeStaticPane pane : this.gui.panes) {
                if (!pane.inPane(xPos, yPos)) {
                    continue;
                }

                for (Slot paneSlot : pane.getSlots()) {
                    if (!(paneSlot instanceof ForgeStaticPane.ForgeStaticPaneDisplayable)) {
                        continue;
                    }

                    if (paneSlot.xPos != xPos || paneSlot.yPos != yPos) {
                        continue;
                    }

                    ((ForgeStaticPane.ForgeStaticPaneDisplayable) paneSlot).getDisplayable().onClick(envyPlayer, clickType);
                }
            }

            return ItemStack.EMPTY;
        }

        private Displayable.ClickType convertClickType(ClickType typeIn) {
            switch(typeIn) {
                case CLONE :
                    return Displayable.ClickType.MIDDLE;
                case PICKUP:
                    return Displayable.ClickType.RIGHT;
                case PICKUP_ALL:
                    return Displayable.ClickType.LEFT;
                default :
                    return null;
            }
        }
    }

    /**
     *
     * Builder implementation for the ForgeGui
     *
     */
    public static final class Builder implements Gui.Builder {

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
}
