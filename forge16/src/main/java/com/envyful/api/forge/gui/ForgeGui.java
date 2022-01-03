package com.envyful.api.forge.gui;

import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.gui.item.EmptySlot;
import com.envyful.api.forge.gui.pane.ForgeSimplePane;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.Gui;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.type.Pair;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * Forge implementation of the {@link Gui} interface.
 *
 */
public class ForgeGui implements Gui {

    private final ITextComponent title;
    private final int height;
    private final PlayerManager<ForgeEnvyPlayer, ServerPlayerEntity> playerManager;
    private final Consumer<ForgeEnvyPlayer> closeConsumer;
    private final ForgeSimplePane parentPane;
    private final ForgeSimplePane[] panes;
    private final ContainerType<?> containerType;

    private final List<ForgeGuiContainer> containers = Lists.newArrayList();

    ForgeGui(String title, int height, PlayerManager<ForgeEnvyPlayer, ServerPlayerEntity> playerManager,
             Consumer<ForgeEnvyPlayer> closeConsumer, Pane... panes) {
        this.title = new StringTextComponent(title);
        this.height = height;
        this.playerManager = playerManager;
        this.closeConsumer = closeConsumer;
        this.parentPane = (ForgeSimplePane) new ForgeSimplePane.Builder().height(height).topLeftX(0).topLeftY(0).width(9).build();
        this.panes = new ForgeSimplePane[panes.length];
        int i = 0;

        for (Pane pane : panes) {
            if (!(pane instanceof ForgeSimplePane)) {
                continue;
            }

            this.panes[i] = (ForgeSimplePane) pane;
            ++i;
        }

        switch(height) {
            default: case 0: case 1: this.containerType = ContainerType.GENERIC_9X1; break;
            case 2: this.containerType = ContainerType.GENERIC_9X2; break;
            case 3: this.containerType = ContainerType.GENERIC_9X3; break;
            case 4: this.containerType = ContainerType.GENERIC_9X4; break;
            case 5: this.containerType = ContainerType.GENERIC_9X5; break;
            case 6: this.containerType = ContainerType.GENERIC_9X6; break;
        }
    }

    @Override
    public void open(EnvyPlayer<?> player) {
        if (!(player instanceof ForgeEnvyPlayer)) {
            return;
        }

        UtilForgeConcurrency.runLater(() -> {
            ServerPlayerEntity parent = ((ForgeEnvyPlayer) player).getParent();

            parent.closeContainer();

            ForgeGuiContainer container = new ForgeGuiContainer(this, parent);

            UtilForgeConcurrency.runWhenTrue(__ -> parent.openContainer == parent.container, () -> {
                parent.openContainer = container;
                parent.currentWindowId = 1;
                parent.connection.sendPacket(new SOpenWindowPacket(parent.currentWindowId, this.getContainerType(), title));
                container.refreshPlayerContents();
                this.containers.add(container);
                ForgeGuiTracker.addGui(player, this);
            });
        }, 1);
    }

    public void update() {
        for (ForgeGuiContainer value : this.containers) {
            value.update(this.panes, false);
        }
    }

    public ContainerType<?> getContainerType() {
        return this.containerType;
    }

    /**
     *
     * Forge container class for the GUI
     *
     */
    private final class ForgeGuiContainer extends Container {

        private final ForgeGui gui;
        private final ServerPlayerEntity player;
        private final List<EmptySlot> emptySlots = Lists.newArrayList();
        private final NonNullList<ItemStack> inventoryItemStacks = NonNullList.create();

        private boolean closed = false;
        private boolean locked = false;

        public ForgeGuiContainer(ForgeGui gui, ServerPlayerEntity player) {
            super(gui.getContainerType(), -1);

            this.gui = gui;
            this.player = player;

            this.update(this.gui.panes, true);
        }

        @Override
        public Slot getSlot(int slotId) {
            if (slotId >= this.inventorySlots.size()) {
                slotId = this.inventorySlots.size() - 1;
            } else if (slotId < 0) {
                slotId = 0;
            }

            return super.getSlot(slotId);
        }

        @Override
        public NonNullList<ItemStack> getInventory() {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for(int i = 0; i < this.inventorySlots.size(); ++i) {
                nonnulllist.add(this.inventorySlots.get(i).getStack());
            }

            return nonnulllist;
        }

        public void update(ForgeSimplePane[] panes, boolean force) {
            this.inventorySlots.clear();
            this.inventoryItemStacks.clear();
            boolean createEmptySlots = this.emptySlots.isEmpty();

            if (!createEmptySlots) {
                this.inventorySlots.addAll(this.emptySlots);
            }

            for (int i = 0; i < (9 * this.gui.height); i++) {
                if (createEmptySlots) {
                    EmptySlot emptySlot = new EmptySlot(this.gui.parentPane, i);

                    this.addSlot(emptySlot);

                    this.emptySlots.add(emptySlot);
                    this.inventorySlots.add(emptySlot);
                }

                this.inventoryItemStacks.add(ItemStack.EMPTY);
            }

            for (ForgeSimplePane pane : panes) {
                if (pane == null) {
                    continue;
                }

                for (int y = 0; y < pane.getItems().length; y++) {
                    ForgeSimplePane.SimpleDisplayableSlot[] row = pane.getItems()[y];

                    for (int x = 0; x < row.length; x++) {
                        ForgeSimplePane.SimpleDisplayableSlot item = row[x];

                        int index = pane.updateIndex((9 * y) + x);

                        this.inventorySlots.set(index, item);
                        this.inventoryItemStacks.set(index, item.getStack());
                    }
                }
            }

            for (int i = 9; i < 36; i++) {
                ItemStack itemStack = player.inventory.mainInventory.get(i);
                inventorySlots.add(new Slot(player.inventory, i, 0, 0));
                inventoryItemStacks.add(itemStack);
            }
            // Sets the slots for the hotbar.
            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = player.inventory.mainInventory.get(i);
                inventorySlots.add(new Slot(player.inventory, i, 0, 0));
                inventoryItemStacks.add(itemStack);
            }

            if (force || ForgeGuiTracker.requiresUpdate(this.player)) {
                this.refreshPlayerContents();
            }
        }

        @Override
        protected Slot addSlot(Slot slotIn) {
            slotIn.slotNumber = this.inventorySlots.size();
            this.inventorySlots.add(slotIn);
            this.inventoryItemStacks.add(ItemStack.EMPTY);
            return slotIn;
        }

        @Override
        public boolean getCanCraft(PlayerEntity player) {
            return true;
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return true;
        }

        @Override
        public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
            this.gui.open(this.gui.playerManager.getPlayer(this.player));
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
        public void detectAndSendChanges() {}

        @Override
        public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, PlayerEntity player) {
            if (slot <= -1 || locked) {
                return ItemStack.EMPTY;
            }

            this.refreshPlayerContents();

            if ((clickTypeIn == ClickType.CLONE && player.isCreative()) || clickTypeIn == ClickType.QUICK_CRAFT) {
                this.clearPlayerCursor();
                return ItemStack.EMPTY;
            }

            Displayable.ClickType clickType = this.convertClickType(dragType, clickTypeIn);

            if (clickType == null) {
                return ItemStack.EMPTY;
            }

            EnvyPlayer<?> envyPlayer = this.gui.playerManager.getPlayer((ServerPlayerEntity) player);

            if (envyPlayer == null) {
                return ItemStack.EMPTY;
            }

            int xPos = slot % 9;
            int yPos = slot / 9;

            for (ForgeSimplePane pane : this.gui.panes) {
                if (!pane.inPane(xPos, yPos)) {
                    continue;
                }

                Pair<Integer, Integer> panePosition = pane.convertXandY(xPos, yPos);

                ForgeSimplePane.SimpleDisplayableSlot simpleDisplayableSlot = pane.getItems()[panePosition.getY()][panePosition.getX()];

                if (simpleDisplayableSlot.getClicks() < 0) {
                    simpleDisplayableSlot.setClicks(simpleDisplayableSlot.getClicks() - 1);

                    if (simpleDisplayableSlot.getClicks() < -100) {
                        locked = true;
                    }

                    return ItemStack.EMPTY;
                }

                if (!simpleDisplayableSlot.shouldClick()) {
                    simpleDisplayableSlot.updateClick();
                    return ItemStack.EMPTY;
                }

                simpleDisplayableSlot.updateClick();

                simpleDisplayableSlot.setClicks(simpleDisplayableSlot.getClicks() - 1);
                simpleDisplayableSlot.getDisplayable().onClick(envyPlayer, clickType);
                ForgeGuiTracker.enqueueUpdate(envyPlayer);
            }

            return ItemStack.EMPTY;
        }

        private Displayable.ClickType convertClickType(int id, ClickType clickType) {
            switch(id) {
                case 0 : return clickType == ClickType.QUICK_MOVE ? Displayable.ClickType.SHIFT_LEFT : Displayable.ClickType.LEFT;
                case 1 : return clickType == ClickType.QUICK_MOVE ? Displayable.ClickType.SHIFT_RIGHT : Displayable.ClickType.RIGHT;
                case 2 : return Displayable.ClickType.MIDDLE;
                default : return null;
            }
        }

        public void refreshPlayerContents() {
            this.player.sendAllContents(this, this.getInventory());
            ForgeGuiTracker.dequeueUpdate(this.player);
            this.player.container.detectAndSendChanges();
            this.player.sendAllContents(this.player.container, this.player.container.getInventory());
        }

        private void clearPlayerCursor() {
            SSetSlotPacket setCursorSlot = new SSetSlotPacket(-1, 0, ItemStack.EMPTY);
            player.connection.sendPacket(setCursorSlot);
        }

        @Override
        public void onContainerClosed(PlayerEntity playerIn) {
            if (this.closed) {
                return;
            }

            this.closed = true;
            super.onContainerClosed(player);

            ServerPlayerEntity sender = (ServerPlayerEntity) playerIn;
            ForgeEnvyPlayer player = this.gui.playerManager.getPlayer(playerIn.getUniqueID());

            int windowId = sender.openContainer.windowId;

            CCloseWindowPacket closeWindowClient = new CCloseWindowPacket();
            ObfuscationReflectionHelper.setPrivateValue(CCloseWindowPacket.class, closeWindowClient, 0,"field_149556_a");
            SCloseWindowPacket closeWindowServer = new SCloseWindowPacket(windowId);

            sender.connection.processCloseWindow(closeWindowClient);
            sender.connection.sendPacket(closeWindowServer);

            if (this.gui.closeConsumer != null) {
                this.gui.closeConsumer.accept(player);
            }

            ForgeGui.this.containers.remove(this);

            sender.currentWindowId = 0;
            sender.container.detectAndSendChanges();
            sender.sendAllContents(sender.container, sender.container.getInventory());

            ForgeGuiTracker.removePlayer(player);
        }
    }
}
