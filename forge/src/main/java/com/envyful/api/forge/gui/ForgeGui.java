package com.envyful.api.forge.gui;

import com.envyful.api.forge.gui.pane.ForgeSimplePane;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 *
 * Forge implementation of the {@link Gui} interface.
 *
 */
public class ForgeGui implements Gui {

    private final ITextComponent title;
    private final int height;
    private final PlayerManager<ForgeEnvyPlayer, EntityPlayerMP> playerManager;
    private final ForgeSimplePane[] panes;

    ForgeGui(String title, int height, PlayerManager<ForgeEnvyPlayer, EntityPlayerMP> playerManager, Pane... panes) {
        this.title = new TextComponentString(title);
        this.height = height;
        this.playerManager = playerManager;
        this.panes = new ForgeSimplePane[panes.length];
        int i = 0;

        for (Pane pane : panes) {
            if (!(pane instanceof ForgeSimplePane)) {
                continue;
            }

            this.panes[i] = (ForgeSimplePane) pane;
            ++i;
        }
    }

    @Override
    public void open(EnvyPlayer<?> player) {
        if (!(player instanceof ForgeEnvyPlayer)) {
            return;
        }

        EntityPlayerMP parent = ((ForgeEnvyPlayer) player).getParent();
        ForgeGuiContainer container = new ForgeGuiContainer(this, parent);

        parent.closeContainer();
        parent.openContainer = container;
        parent.currentWindowId = 1;
        parent.connection.sendPacket(new SPacketOpenWindow(parent.currentWindowId, "minecraft:container", this.title, 9 * this.height));
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
        private final EntityPlayerMP player;

        public ForgeGuiContainer(ForgeGui gui, EntityPlayerMP player) {
            this.windowId = 1;
            this.gui = gui;
            this.player = player;

            this.update(this.gui.panes);
        }

        public void update(ForgeSimplePane[] panes) {
            this.inventorySlots.clear();
            this.inventoryItemStacks.clear();

            for (ForgeSimplePane pane : panes) {
                if (pane == null) {
                    continue;
                }

                inventorySlots.addAll(pane.getSlots());
                inventoryItemStacks.addAll(pane.getDisplayItems());
            }

            for (int i = 9; i < 36; i++) {
                inventorySlots.add(new Slot(this.player.inventory, i - 9, 0, 0));
                inventoryItemStacks.add(this.player.inventory.getStackInSlot(i));
            }

            for (int i = 0; i < 9; i++) {
                inventorySlots.add(new Slot(this.player.inventory, i + 27, 0, 0));
                inventoryItemStacks.add(this.player.inventory.getStackInSlot(i));
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }

        @Override
        public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
            return false;
        }

        @Override
        protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
            return false;
        }

        @Override
        public void putStackInSlot(int slotID, ItemStack stack) {}

        @Override
        public boolean canDragIntoSlot(Slot slotIn) {
            return false;
        }

        @Override
        public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, EntityPlayer player) {
            if (slot <= -1) {
                return ItemStack.EMPTY;
            }

            this.refreshPlayerContents();

            if (clickTypeIn == ClickType.CLONE || clickTypeIn == ClickType.QUICK_CRAFT) {
                this.clearPlayerCursor();
                return ItemStack.EMPTY;
            }

            Displayable.ClickType clickType = this.convertClickType(dragType);

            if (clickType == null) {
                return ItemStack.EMPTY;
            }

            EnvyPlayer<?> envyPlayer = this.gui.playerManager.getPlayer((EntityPlayerMP) player);

            if (envyPlayer == null) {
                return ItemStack.EMPTY;
            }

            int xPos = slot / 9;
            int yPos = slot % 9;

            for (ForgeSimplePane pane : this.gui.panes) {
                if (!pane.inPane(xPos, yPos)) {
                    continue;
                }

                for (Slot paneSlot : pane.getSlots()) {
                    if (!(paneSlot instanceof ForgeSimplePane.SimpleDisplayableSlot)) {
                        continue;
                    }

                    if (paneSlot.xPos != xPos || paneSlot.yPos != yPos) {
                        continue;
                    }

                    ((ForgeSimplePane.SimpleDisplayableSlot) paneSlot).getDisplayable().onClick(envyPlayer, clickType);
                }
            }

            return ItemStack.EMPTY;
        }

        private Displayable.ClickType convertClickType(int id) {
            switch(id) {
                case 0 : return Displayable.ClickType.LEFT;
                case 1 : return Displayable.ClickType.RIGHT;
                case 2 : return Displayable.ClickType.MIDDLE;
                default : return null;
            }
        }

        private void refreshPlayerContents() {
            player.sendAllContents(player.openContainer, inventoryItemStacks);
            player.inventoryContainer.detectAndSendChanges();
            player.sendAllContents(player.inventoryContainer, player.inventoryContainer.inventoryItemStacks);
        }

        private void clearPlayerCursor() {
            SPacketSetSlot setCursorSlot = new SPacketSetSlot(-1, 0, ItemStack.EMPTY);
            player.connection.sendPacket(setCursorSlot);
        }
    }
}
